window.Bank = window.Bank || {};

Bank.http = axios.create({ timeout: 15000 });
Bank.http.interceptors.response.use(function (resp) {
    if (resp.data && resp.data.code === 401) { location.href = '/login'; return Promise.reject(resp.data); }
    return resp;
}, function (err) {
    if (err.response && err.response.status === 401) { location.href = '/login'; }
    alert('请求失败：' + (err.message || err));
    return Promise.reject(err);
});

Bank.dataTable = {
    props: ['url','columns','pk','deleteUrl','params','pageSize'],
    data(){ return {
        rows: [],
        page: 1,
        total: 0,
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
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span><slot name="title"></slot></span>
        <div><slot name="btns"></slot></div>
      </div>
      <slot name="search"></slot>
      <div class="card-body p-0">
        <table class="table table-striped table-hover mb-0 align-middle">
          <thead><tr>
            <th v-for="c in columns">{{c.label}}</th>
            <th v-if="$slots.actions || deleteUrl">操作</th>
          </tr></thead>
          <tbody>
            <tr v-for="(row,idx) in rows" :key="idx">
              <td v-for="c in columns">{{ fmt(row,c) }}</td>
              <td style="white-space:nowrap">
                <slot name="actions" :row="row"></slot>
                <button v-if="deleteUrl" class="btn btn-sm btn-outline-danger" @click="remove(row)">删除</button>
              </td>
            </tr>
            <tr v-if="rows.length===0"><td :colspan="columns.length+1" class="text-center text-muted py-4">暂无数据</td></tr>
          </tbody>
        </table>
      </div>
      <div class="card-footer d-flex justify-content-between align-items-center" v-if="paged">
        <span class="text-muted small">共 {{total}} 条记录，第 {{page}} / {{pages}} 页</span>
        <ul class="pagination pagination-sm mb-0">
          <li class="page-item" :class="{disabled: page<=1}"><a class="page-link" href="javascript:;" @click="go(page-1)">上一页</a></li>
          <template v-for="(p,idx) in pageList" :key="idx">
            <li v-if="p==='...'" class="page-item disabled"><span class="page-link">…</span></li>
            <li v-else class="page-item" :class="{active: p===page}"><a class="page-link" href="javascript:;" @click="go(p)">{{p}}</a></li>
          </template>
          <li class="page-item" :class="{disabled: page>=pages}"><a class="page-link" href="javascript:;" @click="go(page+1)">下一页</a></li>
        </ul>
      </div>
    </div>`,
    mounted(){ this.load(); },
    watch: { params: { deep: true, handler(){ if (this.paged) this.page = 1; } } },
    methods: {
        load(){
            const p = Object.assign({}, this.params || {});
            if (this.paged) { p.pageNum = this.page; p.pageSize = this.size; }
            Bank.http.get(this.url, { params: p }).then(r=>{
                if (this.paged && r.data.data && Array.isArray(r.data.data.records)) {
                    this.rows = r.data.data.records;
                    this.total = r.data.data.total || 0;
                } else {
                    this.rows = (r.data.data && r.data.data.records) ? r.data.data.records : (r.data.data || []);
                }
            });
        },
        reload(){ this.page = 1; this.load(); },
        go(p){ if (p===this.page || p<1 || p>this.pages) return; this.page = p; this.load(); },
        fmt(row,c){
            let v = row[c.f];
            if (c.dict && window.DICTS[c.dict]) return window.DICTS[c.dict][String(v)] ?? v;
            if (v===null||v===undefined) return '';
            return v;
        },
        remove(row){
            if(!confirm('确认删除该记录？')) return;
            let id = row[this.pk||'id'];
            Bank.http.post(this.deleteUrl + '/' + id).then(()=>{ alert('已删除'); this.load(); });
        }
    }
};

// 通用表单：props fields[{name,label,type:text/number/decimal/textarea/select,dict,required}] / url / back
Bank.dataForm = {
    props: ['fields','url','back'],
    data(){ let f={}; this.fields.forEach(x=> f[x.name]=(x.type==='text'||x.type==='textarea')?'':null); return { form:f, DICTS:window.DICTS }; },
    template: `
    <div class="card">
      <div class="card-header"><slot name="title">信息录入</slot></div>
      <div class="card-body">
        <form @submit.prevent="submit" class="row g-3" style="max-width:720px">
          <div class="col-md-6" :class="{'col-12':f.type==='textarea'}" v-for="f in fields" :key="f.name">
            <label class="form-label">{{f.label}}<span class="text-danger" v-if="f.required"> *</span></label>
            <textarea v-if="f.type==='textarea'" rows="3" class="form-control" v-model="form[f.name]"></textarea>
            <select v-else-if="f.type==='select'" class="form-select" v-model="form[f.name]">
              <option :value="null">请选择</option>
              <option v-for="(lab,val) in DICTS[f.dict]" :key="val" :value="Number(val)">{{lab}}</option>
            </select>
            <input v-else class="form-control"
              :type="(f.type==='number'||f.type==='decimal')?'number':'text'"
              :step="f.type==='decimal'?'any':null" v-model="form[f.name]" :required="f.required"/>
          </div>
          <div class="col-12">
            <button class="btn btn-primary" type="submit">保存</button>
            <a class="btn btn-outline-secondary" :href="back">返回列表</a>
          </div>
        </form>
      </div>
    </div>`,
    methods: {
        submit(){
            let p={};
            this.fields.forEach(f=>{
                let v=this.form[f.name];
                if(f.type==='number'||f.type==='decimal'||f.type==='select'){ v=(v===''||v===null)?null:Number(v); }
                p[f.name]=v;
            });
            Bank.http.post(this.url,p).then(r=>{
                if(r.data.code===200){ alert('保存成功'); location.href=this.back; } else alert(r.data.msg);
            });
        }
    }
};

Bank.mount = function(spec, el){
    const app = Vue.createApp(spec);
    app.component('data-table', Bank.dataTable);
    app.component('data-form', Bank.dataForm);
    app.mount(el || '#app');
};
