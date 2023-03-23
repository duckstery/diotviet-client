<template>
  <q-card class="tw-w-1/3 tw-m-auto" flat>
    <q-bar class="bg-primary">
      <IconMage src="images/duck.png"/>
      <q-toolbar-title class="brand tw-ml-3">DiotViet</q-toolbar-title>
      <q-space/>
      <Button flat :icon="castIcon" @click="onYeahAwesome"/>
      <Button flat :icon="batteryIcon" @click="onYeahAwesome"/>
      <Button flat :icon="wifiAndSignalIcon[0]" @click="onYeahAwesome"/>
      <Button flat :icon="wifiAndSignalIcon[1]" @click="onYeahAwesome"/>
      <div class="tw-w-[52px] text-white">{{ time }}</div>
    </q-bar>

    <q-card-section>
      <div class="text-h6 text-brand brand tw-w-fit tw-mx-auto">{{ $t('field.welcome') }}</div>

      <TextField v-model="credential.email" :label="$t('field.username')" icon="fa-solid fa-user" class="tw-mt-4"/>
      <TextField v-model="credential.password" :label="$t('field.password')" icon="fa-solid fa-lock" class="tw-mt-4"
                 type="password"/>
      <div class="tw-flex">
        <q-checkbox
          v-model="remember"
          :label="$t('field.remember_password')"
          checked-icon="task_alt"
          unchecked-icon="highlight_off"
        />
        <q-separator class="tw-ml-2" vertical inset/>
        <div class="tw-ml-2 tw-h-fit tw-my-auto tw-text-blue-600 tw-cursor-pointer">{{
            $t('field.forget_password')
          }}
        </div>
      </div>
    </q-card-section>

    <q-card-actions>
      <q-space/>
      <Button :label="$t('field.manage')" icon="fa-solid fa-chart-line" color="primary" stretch align="around"
              class="tw-w-1/4" @click="onLogin('Manage')"/>
      <Button :label="$t('field.sell')" icon="fa-solid fa-cart-shopping" color="positive" stretch align="around"
              class="tw-w-1/4" @click="onLogin('Work')"/>
    </q-card-actions>
  </q-card>
</template>

<script>
import {date} from "quasar";

import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: 'LoginPanel',

  components: {IconMage, Button, TextField},

  data: () => ({
    credential: {
      email: "ahihi@gmail.com",
      password: "123456",
    },
    remember: true,

    // Display decor
    time: date.formatDate(new Date(), 'HH:mm:ss'),
    counter: 1
  }),

  computed: {
    // Cast icon
    castIcon() {
      return this.counter % 2 === 1 ? 'cast' : 'cast_connected'
    },
    // Battery icon
    batteryIcon() {
      // Get remainder
      const remainder = this.counter % 6;

      return remainder === 0 ? 'battery_charging_full' : `battery_${remainder}_bar`
    },
    // Wifi and signal icon
    wifiAndSignalIcon() {
      // Get remainder
      const remainder = this.counter % 3;

      return remainder === 0
        ? ['wifi', 'signal_cellular_alt']
        : [`wifi_${remainder}_bar`, `signal_cellular_alt_${remainder}_bar`]
    }
  },

  methods: {
    /**
     * On nonsense button click handler
     */
    onYeahAwesome() {
      this.$q.notify({
        message: this.$t('easter.awesome'),
        icon: 'thumb_up', color: 'accent',
        actions: [
          {
            dense: true, icon: 'fa-solid fa-xmark', color: 'white', handler: () => { /* ... */
            }
          }
        ]
      })
    },

    /**
     * On login
     */
    onLogin(location) {
      this.$auth.login(this.credential)
        .then(message => {
          this.$notify(message)
          this.$router.push({name: location})
        })
        .catch(this.$notifyErr)
    },
  },

  mounted() {
    // Make icon turn to live
    setInterval(() => {
      // Update time
      this.time = date.formatDate(Date.now(), 'HH:mm:ss')
      // Update counter
      this.counter = this.counter === 60 ? 1 : this.counter + 1
    }, 1000)
  }
}
</script>
