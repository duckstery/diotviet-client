<template>
  <q-header v-bind="$attrs" reveal class="bg-primary text-white tw-z-[1000]">
    <q-toolbar>
      <IconMage src="images/duck.png" class="tw-cursor-pointer" force-visual rounded @click="$router.push({name: 'Work'})"/>
      <q-toolbar-title v-if="$q.platform.is.capacitor" class="brand">DiotViet</q-toolbar-title>

      <slot/>

      <q-space v-if="!$q.platform.is.capacitor"/>
      <!-- Setting -->
      <Setting flat class="tw-ml-3"/>
      <template v-if="$q.screen.gt.sm">
        <q-item clickable class="tw-w-[121px] tw-h-[32px] tw-p-0 tw-px-3 tw-rounded-lg tw-ml-3">
          <q-item-section side>
            <IconMage src="images/man.png" color="white"/>
          </q-item-section>
          <q-item-section>
            <q-item-label class="tw-font-medium tw-text-ellipsis tw-h-4 tw-max-w-sm tw-line-clamp-1">{{ getUserName }}</q-item-label>
            <q-item-label caption class="text-grey-13">{{ roleDisplayText }}</q-item-label>
          </q-item-section>
          <q-menu>
            <q-list>
              <q-item clickable v-close-popup @click="onChangePassword">
                <q-item-section avatar>
                  <q-icon color="primary" name="fa-solid fa-key"/>
                </q-item-section>
                <q-item-section>{{ $t('field.password') }}</q-item-section>
              </q-item>
              <q-item clickable v-close-popup @click="onLogout">
                <q-item-section avatar>
                  <q-icon color="primary" name="fa-solid fa-right-from-bracket"/>
                </q-item-section>
                <q-item-section>{{ $t('field.logout') }}</q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-item>
      </template>

      <template v-else>
        <!-- More -->
        <Button flat icon="fa-solid fa-ellipsis" color="white" class="tw-ml-3"
                :tooltip="$t('field.more')" @click="drawer = true"/>
      </template>
    </q-toolbar>
  </q-header>

  <NavigateDrawer v-model="drawer" @logout="onLogout" @password="onChangePassword" @close="drawer = false"/>
</template>

<script>
import NavigateDrawer from "components/General/Layout/NavigateDrawer.vue";
import Button from "components/General/Other/Button.vue";
import Setting from "components/General/Layout/Setting.vue";
import IconMage from "components/General/Other/IconMage.vue";
import PasswordPanel from "components/Auth/PasswordPanel.vue";

import {Dialog} from "quasar";
import {useAuthStore} from "stores/auth";
import {mapState} from "pinia";

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
     * On change password
     */
    onChangePassword() {
      Dialog.create({
        component: PasswordPanel,
      })
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
