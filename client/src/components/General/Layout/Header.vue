<template>
  <q-header v-bind="$attrs" reveal class="bg-primary text-white tw-z-[1000]">
    <q-toolbar>
      <IconMage src="/images/duck.png" force-visual rounded/>

      <slot/>

      <q-space/>
      <!-- Setting -->
      <Setting flat class="tw-ml-3"/>
      <!-- Logout -->
      <Button flat icon="fa-solid fa-right-from-bracket" color="white" :tooltip="$t('field.logout')"
              class="tw-ml-3" @click="onLogout"/>

      <q-item clickable class="tw-h-[32px] tw-p-0 tw-px-3 tw-rounded-lg tw-ml-3">
        <q-item-section side>
          <IconMage src="/images/man.png" color="white"/>
        </q-item-section>
        <q-item-section>
          <q-item-label class="tw-font-medium">{{ getUserName }}</q-item-label>
          <q-item-label caption class="text-grey-13">{{ roleDisplayText }}</q-item-label>
        </q-item-section>
      </q-item>

      <!-- Logout -->
      <Button v-if="$q.screen.lt.md" flat icon="fa-solid fa-ellipsis" color="white" :tooltip="$t('field.more')"
              class="tw-ml-3" @click="drawer = true"/>
    </q-toolbar>
  </q-header>

  <NavigateDrawer v-model="drawer" @logout="onLogout" @close="drawer = false"/>
</template>

<script>
import {useAuthStore} from "stores/auth";
import {mapState} from "pinia";

import NavigateDrawer from "components/General/Layout/NavigateDrawer.vue";
import Button from "components/General/Other/Button.vue";
import Setting from "components/General/Layout/Setting.vue";
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: "Header",

  components: {IconMage, Setting, Button, NavigateDrawer},

  data: () => ({
    drawer: false,
  }),

  computed: {
    // Get role display text
    roleDisplayText() {
      return this.$t(`field.role_${this.getPrivilege}`)
    },
    // "Auth" store
    ...mapState(useAuthStore, ['getUserName', 'getPrivilege'])
  },

  methods: {
    /**
     * On logout event handler
     */
    onLogout() {
      this.$util.promptConfirm(this.$t('field.logout'))
        .onOk(this.logout)
    },

    /**
     * Logout logic
     */
    logout() {
      this.$auth.logout()
        .then(res => {
          // Redirect to 'Login'
          this.$router.push({name: 'Login'})
          // Notify
          this.$notify(res)
        })
        .catch(this.$notifyErr)
    }
  }
}
</script>
