window.Bank = window.Bank || {};

/* ==========================================================================
   Toast：替代浏览器自带的 alert
   - 从顶部落下，收回时走同一条路径（空间一致性）
   - 用 Web Animations API 只动 transform / opacity
   - 页面马上跳转时（alert 之后紧跟 location.href），提示会在下一页接着显示
   ========================================================================== */
(function () {
    var KEY = 'bank.toast';
    var host = null;
    var OUT = { opacity: 0, transform: 'translateY(-22px) scale(.96)' };
    var IN  = { opacity: 1, transform: 'translateY(0) scale(1)' };
    var reduce = window.matchMedia && matchMedia('(prefers-reduced-motion: reduce)').matches;

    function guessType(msg) {
        // 以“请”开头或含失败类字眼的算错误，其余视为成功
        return /^请|失败|错误|不一致|不能|不足|无法|必须|不存在|已存在|无权|超出|超过|未|异常/.test(msg) ? 'error' : 'ok';
    }

    function show(msg, type, duration) {
        if (!host) {
            host = document.createElement('div');
            host.className = 'toast-host';
            host.setAttribute('role', 'status');
            host.setAttribute('aria-live', 'polite');
            document.body.appendChild(host);
        }
        var el = document.createElement('div');
        el.className = 'toast-item' + (type === 'error' ? ' error' : '');
        var icon = document.createElement('span');
        icon.className = 'toast-icon';
        icon.textContent = type === 'error' ? '!' : '\u2713';
        var text = document.createElement('span');
        text.textContent = msg;
        el.appendChild(icon);
        el.appendChild(text);
        host.appendChild(el);

        var closed = false;
        function close() {
            if (closed) return;
            closed = true;
            clearTimeout(timer);
            if (!el.animate || reduce) { el.remove(); return; }
            // 从当前位置出发，原路返回
            var a = el.animate([IN, OUT], { duration: 260, easing: 'cubic-bezier(.4,0,1,1)', fill: 'forwards' });
            a.onfinish = function () { el.remove(); };
        }
        if (el.animate && !reduce) {
            el.animate([OUT, IN], { duration: 520, easing: 'cubic-bezier(.2,.8,.2,1)', fill: 'both' });
        }
        el.addEventListener('click', close);
        var timer = setTimeout(close, duration || Math.min(5000, 2200 + msg.length * 60));
    }

    Bank.toast = function (msg, type) {
        msg = String(msg == null ? '' : msg);
        type = type || guessType(msg);
        show(msg, type);
        try {
            sessionStorage.setItem(KEY, JSON.stringify({ m: msg, t: type, at: Date.now() }));
            setTimeout(function () { sessionStorage.removeItem(KEY); }, 800);
        } catch (e) { /* 隐私模式下忽略 */ }
    };

    window.alert = function (msg) { Bank.toast(msg); };

    // 上一页留下的提示
    try {
        var raw = sessionStorage.getItem(KEY);
        if (raw) {
            sessionStorage.removeItem(KEY);
            var p = JSON.parse(raw);
            if (p && Date.now() - p.at < 3000) {
                var run = function () { show(p.m, p.t); };
                if (document.body) run(); else document.addEventListener('DOMContentLoaded', run);
            }
        }
    } catch (e) { /* 忽略 */ }
})();

/* 顶栏：滚动之后才显示底部分隔线，而不是常驻一条硬线 */
(function () {
    var bar = null, ticking = false;
    function update() {
        ticking = false;
        bar = bar || document.querySelector('.topbar');
        if (bar) bar.classList.toggle('scrolled', window.scrollY > 4);
    }
    window.addEventListener('scroll', function () {
        if (!ticking) { ticking = true; requestAnimationFrame(update); }
    }, { passive: true });
})();

Bank.http = axios.create({ timeout: 15000 });
Bank.http.interceptors.response.use(function (resp) {
    if (resp.data && resp.data.code === 401) { location.href = '/login'; return Promise.reject(resp.data); }
    return resp;
}, function (err) {
    if (err.response && err.response.status === 401) { location.href = '/login'; }
    alert('请求失败：' + (err.message || err));
    return Promise.reject(err);
});

// 字典值 → 状态胶囊颜色
Bank.pillClass = function (label) {
    label = String(label == null ? '' : label);
    if (/失败|拒绝|驳回|销户|禁用|已撤销|下架/.test(label)) return 'bad';
    if (/待|未读|冻结/.test(label)) return 'wait';
    if (/^(正常|成功|已通过|已授权|审批通过|已放款|已结清|持仓中|在售|已读)$/.test(label)) return 'ok';
    return '';
};

/* ==========================================================================
   数据格式化（任务 3）：接口原始值 → 人看的文本
   - datetime：后端按 UTC 序列化本地时间，必须固定按东八区显示，否则差 8 小时
   - money：千分位；percent：乘 100 加百分号
   类型按字段名自动猜（guess），列定义里可用 type 手动覆盖。
   guess 不匹配 loanTerm / *Id / idCard / phone / accountNo / applyNo / tradeNo 这类字段。
   ========================================================================== */
Bank.fmt = {
    datetime(v){
        if (v === null || v === undefined || v === '') return '';
        const d = new Date(v);
        if (isNaN(d.getTime())) return String(v);
        const parts = new Intl.DateTimeFormat('en-CA', {
            timeZone: 'Asia/Shanghai', hour12: false,
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit', second: '2-digit'
        }).formatToParts(d).reduce((o, p) => (o[p.type] = p.value, o), {});
        const hh = parts.hour === '24' ? '00' : parts.hour;   // 跨日不能出现 24:00:00
        return parts.year + '-' + parts.month + '-' + parts.day + ' ' + hh + ':' + parts.minute + ':' + parts.second;
    },
    money(v){
        if (v === null || v === undefined || v === '') return '';
        const n = Number(v);
        if (isNaN(n)) return String(v);
        return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    },
    percent(v){
        if (v === null || v === undefined || v === '') return '';
        const n = Number(v);
        if (isNaN(n)) return String(v);
        return (n * 100).toFixed(2) + '%';
    },
    /* 按字段名猜类型；猜不到返回空串，表示不处理 */
    guess(field){
        if (/Time$/.test(field)) return 'datetime';
        if (/^balance$/i.test(field) || /Amount$/.test(field) || /^amount$/i.test(field)) return 'money';
        if (/^rate$/i.test(field) || /Rate$/.test(field)) return 'percent';
        return '';
    }
};

// 通用数据表格：props url / columns[{f,label,dict,type}] / pk / deleteUrl / params / pageSize（可选，传则开启分页）
Bank.dataTable = {
    props: ['url','columns','pk','deleteUrl','params','pageSize'],
    data(){ return {
        rows: [],
        page: 1,
        total: 0,
        loading: true,     // 任务 2：请求期间显示骨架行，避免先闪一下“暂无数据”
        restoredSig: '',   // 任务 9：syncFromUrl 回填后的 params 快照，用来认出“这次变化是自己造成的”
        size: (this.pageSize != null && Number(this.pageSize) > 0) ? Number(this.pageSize) : 10
    }; },
    computed: {
        paged(){ return this.pageSize != null && Number(this.pageSize) > 0; },
        pages(){ return this.paged ? Math.max(1, Math.ceil(this.total / this.size)) : 1; },
        pageList(){
            const n = this.pages, cur = this.page;
            if (n <= 7) return Array.from({length:n}, (_,i)=>i+1);
            const set = new Set([1, n, cur-1, cur, cur+1]);
            const arr = [...set].filter(p=>p>=1 && p<=n).sort((a,b)=>a-b);
            const out = []; let prev = 0;
            for (const p of arr){ if (p - prev > 1) out.push('...'); out.push(p); prev = p; }
            return out;
        }
    },
    template: `
    <div>
      <div class="page-head">
        <h1 class="page-title"><slot name="title"></slot></h1>
        <div><slot name="btns"></slot></div>
      </div>
      <div class="card">
        <slot name="search"></slot>
        <div class="card-body p-0">
          <table class="table table-hover mb-0 align-middle">
            <thead><tr>
              <th v-for="c in columns" :class="cellClass(c)">{{c.label}}</th>
              <th v-if="$slots.actions || deleteUrl">操作</th>
            </tr></thead>
            <tbody>
              <tr v-for="(row,idx) in rows" :key="idx">
                <td v-for="c in columns" :class="cellClass(c)">
                  <span v-if="c.dict" class="pill" :class="pillClass(row,c)">{{ fmt(row,c) }}</span>
                  <template v-else>{{ fmt(row,c) }}</template>
                </td>
                <td style="white-space:nowrap">
                  <slot name="actions" :row="row"></slot>
                  <button v-if="deleteUrl" class="btn btn-sm btn-outline-danger" @click="remove(row)">删除</button>
                </td>
              </tr>
              <template v-if="loading && rows.length===0">
                <tr v-for="n in 3" :key="'sk'+n" class="skeleton-row">
                  <td v-for="c in columns" :key="c.f"><span class="skeleton"></span></td>
                  <td v-if="$slots.actions || deleteUrl"><span class="skeleton short"></span></td>
                </tr>
              </template>
              <tr v-if="!loading && rows.length===0"><td :colspan="columns.length+1" class="empty">暂无数据</td></tr>
            </tbody>
          </table>
        </div>
        <div class="card-footer d-flex justify-content-between align-items-center" v-if="paged">
          <span class="text-muted small">共 {{total}} 条 · 第 {{page}} / {{pages}} 页</span>
          <ul class="pagination pagination-sm mb-0">
            <li class="page-item" :class="{disabled: page<=1}"><button type="button" class="page-link" :disabled="page<=1" @click="go(page-1)">上一页</button></li>
            <template v-for="(p,idx) in pageList" :key="idx">
              <li v-if="p==='...'" class="page-item disabled"><span class="page-link">…</span></li>
              <li v-else class="page-item" :class="{active: p===page}"><button type="button" class="page-link" :aria-current="p===page ? 'page' : null" @click="go(p)">{{p}}</button></li>
            </template>
            <li class="page-item" :class="{disabled: page>=pages}"><button type="button" class="page-link" :disabled="page>=pages" @click="go(page+1)">下一页</button></li>
          </ul>
        </div>
      </div>
    </div>`,
    mounted(){
        // 任务 9：先回填地址栏里的筛选条件与页码，再发第一次请求
        this.syncFromUrl();
        this.load();
    },
    watch: { params: { deep: true, handler(){
        // 任务 9：syncFromUrl 回填 params 同样会触发这个 watch。用参数快照认出那一次并跳过，
        // 否则回填完页码马上被重置为 1。只跳过与快照完全相同的一次（Vue 的调度器对同一个
        // watcher 在一次刷新里只排队一次，所以后面真正的用户改动一定拿到不同的快照）。
        const sig = JSON.stringify(this.params || {});
        if (this.restoredSig && sig === this.restoredSig) { this.restoredSig = ''; return; }
        if (this.paged) this.page = 1;
    } } },
    methods: {
        load(){
            this.loading = true;
            const p = Object.assign({}, this.params || {});
            if (this.paged) { p.pageNum = this.page; p.pageSize = this.size; }
            Bank.http.get(this.url, { params: p }).then(r=>{
                if (this.paged && r.data.data && Array.isArray(r.data.data.records)) {
                    this.rows = r.data.data.records;
                    this.total = r.data.data.total || 0;
                } else {
                    this.rows = (r.data.data && r.data.data.records) ? r.data.data.records : (r.data.data || []);
                }
                this.syncToUrl();   // 任务 9：成功之后把筛选条件与页码写回地址栏
            }).finally(() => { this.loading = false; });
        },
        /* 任务 9：把地址栏里的筛选条件与页码回填进 params / page。
           只回填 params 里本来就有的键；数字型状态下拉框（初始值是 null）转回 Number，空值/null 表示未选。 */
        syncFromUrl(){
            if (typeof location === 'undefined' || !location.search) return;
            const q = new URLSearchParams(location.search);
            const params = this.params || {};
            let changed = false;
            Object.keys(params).forEach(k => {
                if (!q.has(k)) return;
                const raw = q.get(k);
                const cur = params[k];
                let v = raw;
                if (cur === null && (raw === '' || raw === 'null')) {
                    v = null;                                                    // 未选
                } else if (typeof cur === 'number' || (cur === null && /^-?\d+(\.\d+)?$/.test(raw))) {
                    v = Number(raw);                                             // 数字型下拉框转回 Number
                    if (isNaN(v)) v = null;
                }
                if (v !== cur) { params[k] = v; changed = true; }
            });
            // 顺序：先回填 params，再设置页码；并记下回填后的快照，让 watch 跳过这一次页码归 1
            if (changed) this.restoredSig = JSON.stringify(params);
            if (this.paged) {
                const p = parseInt(q.get('page'), 10);
                if (!isNaN(p) && p > 0) this.page = p;
            }
        },
        /* 任务 9：用 replaceState 把非空的筛选条件写回地址栏；页码只在 >1 时写 */
        syncToUrl(){
            if (typeof history === 'undefined' || !history.replaceState) return;
            const q = new URLSearchParams();
            const params = this.params || {};
            Object.keys(params).forEach(k => {
                const v = params[k];
                if (v === null || v === undefined || v === '') return;
                q.set(k, String(v));
            });
            if (this.paged && this.page > 1) q.set('page', String(this.page));
            const qs = q.toString();
            history.replaceState(null, '', location.pathname + (qs ? '?' + qs : '') + (location.hash || ''));
        },
        reload(){ this.page = 1; this.load(); },
        go(p){ if (p===this.page || p<1 || p>this.pages) return; this.page = p; this.load(); },
        fmt(row,c){
            let v = row[c.f];
            // 字典列（状态胶囊）优先级最高，不能被格式化覆盖
            if (c.dict && window.DICTS[c.dict]) return window.DICTS[c.dict][String(v)] ?? v;
            const t = c.type || Bank.fmt.guess(c.f);
            if (t && typeof Bank.fmt[t] === 'function') return Bank.fmt[t](v);
            if (v===null||v===undefined) return '';
            return v;
        },
        /* 任务 3：金额 / 利率列右对齐 */
        cellClass(c){
            const t = c.type || Bank.fmt.guess(c.f);
            return (t === 'money' || t === 'percent') ? 'num' : '';
        },
        pillClass(row,c){ return Bank.pillClass(this.fmt(row,c)); },
        remove(row){
            Bank.confirm('确认删除该记录？', {danger:true, okText:'删除'}).then(ok=>{
                if(!ok) return;
                let id = row[this.pk||'id'];
                Bank.http.post(this.deleteUrl + '/' + id).then(()=>{ alert('已删除'); this.load(); });
            });
        }
    }
};

// 通用表单：props fields[{name,label,type:text/number/decimal/textarea/select,dict,required,pattern,message}] / url / back
// pattern 是正则字符串，message 是校验失败时的错误文案（不填则用默认文案）
Bank.dataForm = {
    props: ['fields','url','back'],
    data(){
        let f={};
        this.fields.forEach(x=> f[x.name]=(x.type==='text'||x.type==='textarea')?'':null);
        return { form:f, DICTS:window.DICTS, errors:{}, busy:false };
    },
    template: `
    <div>
      <div class="page-head">
        <h1 class="page-title"><slot name="title">信息录入</slot></h1>
      </div>
      <div class="card" style="max-width:760px">
        <div class="card-body" style="padding:26px 28px">
          <form @submit.prevent="submit" class="row g-3" novalidate>
            <div class="col-md-6" :class="{'col-12':f.type==='textarea'}" v-for="f in fields" :key="f.name">
              <label class="form-label">{{f.label}}<span class="text-danger" v-if="f.required"> *</span></label>
              <textarea v-if="f.type==='textarea'" rows="3" class="form-control" :class="{'is-invalid':errors[f.name]}"
                v-model="form[f.name]" @blur="check(f)"></textarea>
              <select v-else-if="f.type==='select'" class="form-select" :class="{'is-invalid':errors[f.name]}"
                v-model="form[f.name]" @blur="check(f)">
                <option :value="null">请选择</option>
                <option v-for="(lab,val) in DICTS[f.dict]" :key="val" :value="Number(val)">{{lab}}</option>
              </select>
              <input v-else class="form-control" :class="{'is-invalid':errors[f.name]}"
                :type="(f.type==='number'||f.type==='decimal')?'number':'text'"
                :step="f.type==='decimal'?'any':null" v-model="form[f.name]" :required="f.required" @blur="check(f)"/>
              <div class="field-error" v-if="errors[f.name]">{{errors[f.name]}}</div>
            </div>
            <div class="col-12 pt-2 d-flex gap-2">
              <button class="btn btn-primary" type="submit" :disabled="busy">{{ busy ? '保存中…' : '保存' }}</button>
              <a class="btn btn-outline-secondary" :href="back">返回列表</a>
            </div>
          </form>
        </div>
      </div>
    </div>`,
    methods: {
        // 失焦校验单个字段：不合法写入 errors[f.name]，合法则清除
        check(f){
            const v = this.form[f.name];
            const empty = (v === null || v === undefined || v === '');
            let msg = '';
            if (empty) {
                if (f.required) msg = f.message || ('请填写' + f.label);
            } else if (f.pattern && !new RegExp(f.pattern).test(String(v))) {
                msg = f.message || '格式不正确';
            }
            if (msg) this.errors[f.name] = msg; else delete this.errors[f.name];
            return !msg;
        },
        // 校验全部字段，返回第一个出错字段的 name（没有错误返回空串）
        checkAll(){
            let first = '';
            this.fields.forEach(f => { if (!this.check(f) && !first) first = f.name; });
            return first;
        },
        submit(){
            if (this.busy) return;
            if (this.checkAll()) {
                const el = this.$el.querySelector('.is-invalid');
                if (el && el.focus) el.focus();
                return;
            }
            this.busy = true;
            let p={};
            this.fields.forEach(f=>{
                let v=this.form[f.name];
                if(f.type==='number'||f.type==='decimal'||f.type==='select'){ v=(v===''||v===null)?null:Number(v); }
                p[f.name]=v;
            });
            Bank.http.post(this.url,p).then(r=>{
                if(r.data.code===200){ alert('保存成功'); location.href=this.back; }
                else { this.busy=false; alert(r.data.msg); }
            }).catch(()=>{ this.busy=false; });
        }
    }
};

Bank.mount = function(spec, el){
    const app = Vue.createApp(spec);
    app.component('data-table', Bank.dataTable);
    app.component('data-form', Bank.dataForm);
    app.mount(el || '#app');
};
