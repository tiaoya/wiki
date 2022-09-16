import { createStore } from 'vuex'

declare let SessionStorage: any;
const USER = "USER";


const store = createStore({
  state: {
    // 刷新的时候避免空指针
    user: SessionStorage.get(USER) || {}
  },
  getters: {
  },
  mutations: {
    setUser(state,user){
      console.log("store user：", user);
      state.user = user;
      SessionStorage.set(USER, user);
    }
  },
  actions: {
  },
  modules: {
  }
})
export default store;