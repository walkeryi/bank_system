/* ==========================================================================
   对话框：替代浏览器自带的 confirm / prompt
   - Bank.confirm(message, options)            → Promise<boolean>   确认 true / 取消、Esc、点遮罩 false
   - Bank.prompt(message, defaultValue, options) → Promise<string|null> 确认字符串 / 取消 null
   - options：title（可选标题）、okText（默认“确定”）、cancelText（默认“取消”）、danger（确认按钮变红）
   - 入场 / 退场用 Web Animations API，只动 transform / opacity
   - 同一时间只允许一个对话框；再次调用时旧的按“取消”结束并立即移除
   ========================================================================== */
(function () {
    'use strict';

    var EASE = 'cubic-bezier(.2,.8,.2,1)';
    var DUR_IN = 280;
    var DUR_OUT = 180;
    var IN = { opacity: 1, transform: 'scale(1)' };
    var OUT = { opacity: 0, transform: 'scale(.96)' };
    var seq = 0;
    var active = null;

    function reduceMotion() {
        return !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
    }

    // 收尾：卸监听、移除节点；只有自己还是当前对话框时才恢复滚动和焦点
    function cleanup(d) {
        if (d.cleaned) return;
        d.cleaned = true;
        document.removeEventListener('keydown', d.onKey, true);
        if (d.mask.parentNode) d.mask.parentNode.removeChild(d.mask);
        if (active === d) {
            active = null;
            document.body.style.overflow = d.prevOverflow;
            if (d.lastFocus && typeof d.lastFocus.focus === 'function') {
                try { d.lastFocus.focus(); } catch (e) { /* 元素可能已从文档里移除 */ }
            }
        }
    }

    // 播放退场动画后收尾；动画被中断时用定时器兜底
    function close(d, value) {
        if (d.closing) return;
        d.closing = true;
        if (reduceMotion() || !d.panel.animate) {
            cleanup(d);
            d.resolve(value);
            return;
        }
        var a = d.panel.animate([IN, OUT], { duration: DUR_OUT, easing: EASE, fill: 'forwards' });
        d.mask.animate([{ opacity: 1 }, { opacity: 0 }], { duration: DUR_OUT, easing: 'ease', fill: 'forwards' });
        var done = false;
        function end() {
            if (done) return;
            done = true;
            cleanup(d);
            d.resolve(value);
        }
        a.onfinish = end;
        setTimeout(end, DUR_OUT + 200);
    }

    // 被新对话框顶掉：按“取消”结束，立即移除
    function abort(d) {
        if (d.closing) return;
        d.closing = true;
        cleanup(d);
        d.resolve(d.cancelValue);
    }

    function open(cfg) {
        return new Promise(function (resolve) {
            if (active) abort(active);

            var id = ++seq;
            var titleId = 'dlg-title-' + id;
            var msgId = 'dlg-msg-' + id;
            var hasInput = cfg.input !== null;
            var d = {
                resolve: resolve,
                cancelValue: hasInput ? null : false,
                lastFocus: document.activeElement,
                prevOverflow: document.body.style.overflow || '',
                closing: false,
                cleaned: false
            };

            var mask = document.createElement('div');
            mask.className = 'dlg-mask';

            var panel = document.createElement('div');
            panel.className = 'dlg-panel';
            panel.setAttribute('role', 'dialog');
            panel.setAttribute('aria-modal', 'true');
            panel.setAttribute('aria-describedby', msgId);
            if (cfg.title) panel.setAttribute('aria-labelledby', titleId);
            else panel.setAttribute('aria-label', cfg.message);

            var body = document.createElement('div');
            body.className = 'dlg-body';

            if (cfg.title) {
                var h = document.createElement('h2');
                h.className = 'dlg-title';
                h.id = titleId;
                h.textContent = cfg.title;
                body.appendChild(h);
            }

            var msg = document.createElement('p');
            msg.className = 'dlg-msg';
            msg.id = msgId;
            msg.textContent = cfg.message;
            body.appendChild(msg);

            var input = null;
            if (hasInput) {
                input = document.createElement('input');
                input.type = 'text';
                input.className = 'form-control dlg-input';
                input.value = cfg.input;
                body.appendChild(input);
            }

            var actions = document.createElement('div');
            actions.className = 'dlg-actions';

            var cancelBtn = document.createElement('button');
            cancelBtn.type = 'button';
            cancelBtn.className = 'dlg-btn dlg-cancel';
            cancelBtn.textContent = cfg.cancelText;

            var okBtn = document.createElement('button');
            okBtn.type = 'button';
            okBtn.className = 'dlg-btn dlg-ok' + (cfg.danger ? ' danger' : '');
            okBtn.textContent = cfg.okText;

            actions.appendChild(cancelBtn);
            actions.appendChild(okBtn);
            panel.appendChild(body);
            panel.appendChild(actions);
            mask.appendChild(panel);

            d.mask = mask;
            d.panel = panel;

            function readOk() { return input ? input.value : true; }
            function readCancel() { return input ? null : false; }

            cancelBtn.addEventListener('click', function () { close(d, readCancel()); });
            okBtn.addEventListener('click', function () { close(d, readOk()); });
            mask.addEventListener('mousedown', function (e) {
                if (e.target === mask) close(d, readCancel());
            });

            d.onKey = function (e) {
                if (e.isComposing || e.keyCode === 229) return;   // 输入法组字中，不算确认
                if (e.key === 'Escape') { e.preventDefault(); close(d, readCancel()); return; }
                if (e.key === 'Enter') {
                    if (e.target && e.target.tagName === 'BUTTON') return;  // 交给按钮自己的 click
                    e.preventDefault();
                    close(d, readOk());
                    return;
                }
                if (e.key === 'Tab') {   // 焦点不跑出面板
                    var items = panel.querySelectorAll('button, input');
                    if (!items.length) return;
                    var first = items[0], last = items[items.length - 1];
                    var inside = panel.contains(document.activeElement);
                    if (e.shiftKey && (!inside || document.activeElement === first)) {
                        e.preventDefault(); last.focus();
                    } else if (!e.shiftKey && (!inside || document.activeElement === last)) {
                        e.preventDefault(); first.focus();
                    }
                }
            };
            document.addEventListener('keydown', d.onKey, true);

            document.body.appendChild(mask);
            document.body.style.overflow = 'hidden';

            if (!reduceMotion() && panel.animate) {
                panel.animate([OUT, IN], { duration: DUR_IN, easing: EASE, fill: 'both' });
                mask.animate([{ opacity: 0 }, { opacity: 1 }], { duration: DUR_IN, easing: 'ease', fill: 'both' });
            }

            if (input) { input.focus(); input.select(); }
            else { okBtn.focus(); }

            active = d;
        });
    }

    function opts(options) {
        var o = options || {};
        return {
            title: o.title ? String(o.title) : '',
            okText: o.okText ? String(o.okText) : '确定',
            cancelText: o.cancelText ? String(o.cancelText) : '取消',
            danger: !!o.danger
        };
    }

    Bank.confirm = function (message, options) {
        var c = opts(options);
        c.message = message == null ? '' : String(message);
        c.input = null;
        return open(c);
    };

    Bank.prompt = function (message, defaultValue, options) {
        var c = opts(options);
        c.message = message == null ? '' : String(message);
        c.input = defaultValue == null ? '' : String(defaultValue);
        return open(c);
    };
})();
