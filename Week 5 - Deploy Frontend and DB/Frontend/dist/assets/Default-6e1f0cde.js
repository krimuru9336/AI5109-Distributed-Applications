import{p as A,i as Z,c as v,r as ae,a as z,b as C,g as se,s as le,o as ue,d as ie,f as re,e as q,h as b,j as D,k as ce,l as K,m as X,w as F,n as ve,q as pe,u as de}from"./index-2856e7d8.js";import{u as fe,m as U,a as me,b as ye,c as W}from"./tag-bbbf20c8.js";const k=Symbol.for("vuetify:layout"),ge=Symbol.for("vuetify:layout-item"),N=1e3,he=A({overlaps:{type:Array,default:()=>[]},fullHeight:Boolean},"layout");function be(){const e=Z(k);if(!e)throw new Error("[Vuetify] Could not find injected layout");return{getLayoutItem:e.getLayoutItem,mainRect:e.mainRect,mainStyles:e.mainStyles}}const _e=(e,c,n,s)=>{let l={top:0,left:0,right:0,bottom:0};const u=[{id:"",layer:{...l}}];for(const i of e){const f=c.get(i),y=n.get(i),g=s.get(i);if(!f||!y||!g)continue;const _={...l,[f.value]:parseInt(l[f.value],10)+(g.value?parseInt(y.value,10):0)};u.push({id:i,layer:_}),l=_}return u};function xe(e){const c=Z(k,null),n=v(()=>c?c.rootZIndex.value-100:N),s=ae([]),l=z(new Map),u=z(new Map),i=z(new Map),f=z(new Map),y=z(new Map),{resizeRef:g,contentRect:_}=fe(),Y=v(()=>{const o=new Map,p=e.overlaps??[];for(const t of p.filter(r=>r.includes(":"))){const[r,a]=t.split(":");if(!s.value.includes(r)||!s.value.includes(a))continue;const m=l.get(r),h=l.get(a),S=u.get(r),V=u.get(a);!m||!h||!S||!V||(o.set(a,{position:m.value,amount:parseInt(S.value,10)}),o.set(r,{position:h.value,amount:-parseInt(V.value,10)}))}return o}),x=v(()=>{const o=[...new Set([...i.values()].map(t=>t.value))].sort((t,r)=>t-r),p=[];for(const t of o){const r=s.value.filter(a=>{var m;return((m=i.get(a))==null?void 0:m.value)===t});p.push(...r)}return _e(p,l,u,f)}),O=v(()=>!Array.from(y.values()).some(o=>o.value)),I=v(()=>x.value[x.value.length-1].layer),G=v(()=>({"--v-layout-left":C(I.value.left),"--v-layout-right":C(I.value.right),"--v-layout-top":C(I.value.top),"--v-layout-bottom":C(I.value.bottom),...O.value?void 0:{transition:"none"}})),w=v(()=>x.value.slice(1).map((o,p)=>{let{id:t}=o;const{layer:r}=x.value[p],a=u.get(t),m=l.get(t);return{id:t,...r,size:Number(a.value),position:m.value}})),T=o=>w.value.find(p=>p.id===o),L=se("createLayout"),B=le(!1);ue(()=>{B.value=!0}),ie(k,{register:(o,p)=>{let{id:t,order:r,position:a,layoutSize:m,elementSize:h,active:S,disableTransitions:V,absolute:ee}=p;i.set(t,r),l.set(t,a),u.set(t,m),f.set(t,S),V&&y.set(t,V);const H=re(ge,L==null?void 0:L.vnode).indexOf(o);H>-1?s.value.splice(H,0,t):s.value.push(t);const E=v(()=>w.value.findIndex($=>$.id===t)),M=v(()=>n.value+x.value.length*2-E.value*2),te=v(()=>{const $=a.value==="left"||a.value==="right",R=a.value==="right",ne=a.value==="bottom",j={[a.value]:0,zIndex:M.value,transform:`translate${$?"X":"Y"}(${(S.value?0:-110)*(R||ne?-1:1)}%)`,position:ee.value||n.value!==N?"absolute":"fixed",...O.value?void 0:{transition:"none"}};if(!B.value)return j;const d=w.value[E.value];if(!d)throw new Error(`[Vuetify] Could not find layout item "${t}"`);const P=Y.value.get(t);return P&&(d[P.position]+=P.amount),{...j,height:$?`calc(100% - ${d.top}px - ${d.bottom}px)`:h.value?`${h.value}px`:void 0,left:R?void 0:`${d.left}px`,right:R?`${d.right}px`:void 0,top:a.value!=="bottom"?`${d.top}px`:void 0,bottom:a.value!=="top"?`${d.bottom}px`:void 0,width:$?h.value?`${h.value}px`:void 0:`calc(100% - ${d.left}px - ${d.right}px)`}}),oe=v(()=>({zIndex:M.value-1}));return{layoutItemStyles:te,layoutItemScrimStyles:oe,zIndex:M}},unregister:o=>{i.delete(o),l.delete(o),u.delete(o),f.delete(o),y.delete(o),s.value=s.value.filter(p=>p!==o)},mainRect:I,mainStyles:G,getLayoutItem:T,items:w,layoutRect:_,rootZIndex:n});const J=v(()=>["v-layout",{"v-layout--full-height":e.fullHeight}]),Q=v(()=>({zIndex:c?n.value:void 0,position:c?"relative":void 0,overflow:c?"hidden":void 0}));return{layoutClasses:J,layoutStyles:Q,getLayoutItem:T,items:w,layoutRect:_,layoutRef:g}}const Ie=A({scrollable:Boolean,...U(),...me({tag:"main"})},"VMain"),we=q()({name:"VMain",props:Ie(),setup(e,c){let{slots:n}=c;const{mainStyles:s}=be(),{ssrBootStyles:l}=ye();return W(()=>b(e.tag,{class:["v-main",{"v-main--scrollable":e.scrollable},e.class],style:[s.value,l.value,e.style]},{default:()=>{var u,i;return[e.scrollable?b("div",{class:"v-main__scroller"},[(u=n.default)==null?void 0:u.call(n)]):(i=n.default)==null?void 0:i.call(n)]}})),{}}}),Se=D({__name:"View",setup(e){return(c,n)=>{const s=ce("router-view");return K(),X(we,null,{default:F(()=>[b(s)]),_:1})}}});const Ve=A({...U(),...he({fullHeight:!0}),...ve()},"VApp"),$e=q()({name:"VApp",props:Ve(),setup(e,c){let{slots:n}=c;const s=pe(e),{layoutClasses:l,getLayoutItem:u,items:i,layoutRef:f}=xe(e),{rtlClasses:y}=de();return W(()=>{var g;return b("div",{ref:f,class:["v-application",s.themeClasses.value,l.value,y.value,e.class],style:[e.style]},[b("div",{class:"v-application__wrap"},[(g=n.default)==null?void 0:g.call(n)])])}),{getLayoutItem:u,items:i,theme:s}}}),Me=D({__name:"Default",setup(e){return(c,n)=>(K(),X($e,null,{default:F(()=>[b(Se)]),_:1}))}});export{Me as default};
