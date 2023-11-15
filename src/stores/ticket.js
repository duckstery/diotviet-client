import { defineStore } from 'pinia';

export const useTicketStore = defineStore('ticket', {
  state: () => ({
    template: '',
    tags: []
  }),
});
