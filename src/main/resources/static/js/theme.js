window.Bank = window.Bank || {};

/* ==========================================================================
   外观（明暗主题）：light 浅色 / dark 深色 / system 跟随系统
   - 选择存在 localStorage 的 dsh-theme，刷新后保持
   - 选择“跟随系统”时监听 prefers-color-scheme 的变化，系统明暗一改就跟着切
   - 隐私模式下 localStorage 可能抛错，所有访问都包 try/catch
   首帧由 fragments.html 头部的内联脚本先画一次（不依赖本文件），避免闪白屏；
   这里负责随后的系统监听、下拉框初始化和切换逻辑。
   ========================================================================== */
(function () {
    var KEY = 'dsh-theme';
    var VALUES = { light: 1, dark: 1, system: 1 };   // 合法取值，同时当白名单用
    var SELECTOR = '[data-theme-select]';            // 顶栏里的外观下拉框
    var mq = window.matchMedia ? window.matchMedia('(prefers-color-scheme: dark)') : null;
    var listening = false;
    var watchers = [];

    /* 读保存的选择；没保存过、或者值被改坏了，都当作“跟随系统” */
    function stored() {
        try {
            var v = localStorage.getItem(KEY);
            return VALUES[v] ? v : 'system';
        } catch (e) {
            return 'system';   // 隐私模式读不到：按跟随系统处理，不影响页面
        }
    }

    function save(v) {
        try { localStorage.setItem(KEY, v); } catch (e) { /* 隐私模式：这次选择只在本次会话生效 */ }
    }

    /* 外观选择 → 实际生效的明暗 */
    function resolve(pref) {
        if (pref === 'light' || pref === 'dark') return pref;
        return (mq && mq.matches) ? 'dark' : 'light';
    }

    /* 把下拉框的选中项同步成当前保存的选择 */
    function syncSelects() {
        var pref = stored();
        var list = document.querySelectorAll(SELECTOR);
        for (var i = 0; i < list.length; i++) list[i].value = pref;
    }

    /* 在系统明暗变化时重画：只在“跟随系统”时生效，用户选了浅色/深色就不动 */
    function onSystemChange() {
        if (stored() === 'system') apply();
    }

    function listen() {
        if (listening || !mq) return;
        listening = true;
        if (mq.addEventListener) mq.addEventListener('change', onSystemChange);
        else if (mq.addListener) mq.addListener(onSystemChange);   // 老浏览器没有 addEventListener
    }

    /* 给每个外观下拉框接上 change 事件（用元素上的标记避免重复绑定） */
    function bindSelects() {
        var list = document.querySelectorAll(SELECTOR);
        for (var i = 0; i < list.length; i++) {
            var el = list[i];
            if (el.__bankThemeBound) continue;
            el.__bankThemeBound = true;
            el.addEventListener('change', function () { set(this.value); });
        }
    }

    /* 把当前保存的选择立刻应用到页面（幂等：重复调用结果一致） */
    function apply() {
        listen();
        var pref = stored();
        document.documentElement.setAttribute('data-bs-theme', resolve(pref));
        syncSelects();
        return pref;
    }

    function get() { return stored(); }

    /* 保存选择并立即生效；传了非法值则不改动已保存的选择 */
    function set(value) {
        if (!VALUES[value]) return stored();
        save(value);
        apply();
        for (var i = 0; i < watchers.length; i++) {
            try { watchers[i](value); } catch (e) { /* 订阅者出错不影响主题 */ }
        }
        return value;
    }

    Bank.theme = {
        KEY: KEY,
        get: get,
        set: set,
        apply: apply,
        /* 订阅外观选择的变化，返回取消订阅的函数 */
        onChange: function (fn) {
            if (typeof fn !== 'function') return function () { };
            watchers.push(fn);
            return function () {
                var i = watchers.indexOf(fn);
                if (i >= 0) watchers.splice(i, 1);
            };
        }
    };

    function init() {
        listen();
        bindSelects();
        apply();
    }

    // 顶栏是内联在页面里的，而本文件在 body 末尾才加载：等 DOM 就绪再初始化下拉框
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
    else init();
})();
