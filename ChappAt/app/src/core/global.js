import {create} from 'zustand';

const useGlobalStore = create(set => ({
  authenticated: false,
  user: {},
  login: user => {
    set(state => ({
      authenticated: true,
      user: user,
    }));
  },

  logout: () => {
    set(state => ({
      authenticated: false,
      user: {},
    }));
  },
}));
export default useGlobalStore;
