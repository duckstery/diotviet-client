<template>
  <q-header v-bind="$attrs" reveal class="bg-primary text-white">
    <q-toolbar>
      <IconMage src="images/duck.png" rounded/>
      <TextField v-model="search" class="tw-w-1/4 tw-max-w-lg tw-ml-4"/>
      <OrderBar/>
      <q-space/>
      <Setting/>
      <q-item clickable class="tw-h-[32px] tw-p-0 tw-px-3 tw-rounded-lg tw-ml-3">
        <q-item-section side>
          <IconMage src="images/man.png"/>
        </q-item-section>
        <q-item-section>
          <q-item-label class="tw-font-medium">{{ getUserName }}</q-item-label>
          <q-item-label caption class="text-grey-13">{{ roleDisplayText }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-toolbar>
  </q-header>

  <NavigateDrawer/>
</template>

<script>
import {useAuthStore} from "stores/auth";
import {mapState} from "pinia";

import NavigateDrawer from "components/General/Layout/NavigateDrawer.vue";
import TextField from "components/General/Other/TextField.vue";
import OrderBar from "components/General/Layout/OrderBar.vue";
import Button from "components/General/Other/Button.vue";
import Setting from "components/General/Layout/Setting.vue";
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: "Header",

  components: {IconMage, Setting, Button, OrderBar, TextField, NavigateDrawer},

  data: () => ({
    search: '',
  }),

  computed: {
    // Get role display text
    roleDisplayText() {
      return this.$t(`field.role_${this.getPrivilege}`)
    },
    // "Auth" store
    ...mapState(useAuthStore, ['getUserName', 'getPrivilege'])
  }
}
</script>
