<template>
  <q-card class="tw-w-1/3 tw-m-auto">
    <q-toolbar class="bg-primary text-white">
      <q-avatar>
        <img src="/images/duck.png">
      </q-avatar>
      <q-toolbar-title class="text-h5 brand tw-ml-3">DiotViet</q-toolbar-title>
      <q-space/>
      <Button icon="fa-solid fa-hand-sparkles" round stretch flat @click="onBlinkBlink"/>
      <Button icon="fa-solid fa-wand-magic-sparkles" round stretch flat @click="onBlinkBlink"/>
      <Button icon="fa-solid fa-spray-can-sparkles" round stretch flat @click="onBlinkBlink"/>
    </q-toolbar>

    <q-card-section>
      <div class="text-h6 text-dark brand tw-w-fit tw-mx-auto">{{ $t('field.welcome') }}</div>

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
        <div class="tw-ml-2 tw-h-fit tw-my-auto tw-text-blue-600 tw-cursor-pointer">{{ $t('field.forget_password') }}</div>
      </div>
    </q-card-section>

    <q-card-actions>
      <q-space/>
      <Button :label="$t('field.manage')" icon="fa-solid fa-chart-line" color="primary" stretch align="around"
              class="tw-w-1/4" @click="onLogin"/>
      <Button :label="$t('field.sell')" icon="fa-solid fa-cart-shopping" color="positive" stretch align="around"
              class="tw-w-1/4" @click="onLogin"/>
    </q-card-actions>
  </q-card>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";

export default {
  name: 'LoginPanel',

  components: {Button, TextField},

  data: () => ({
    credential: {
      email: "ahihi@gmail.com",
      password: "123456",
    },
    remember: true,
  }),

  methods: {
    /**
     * On nonsense button click handler
     */
    onBlinkBlink() {
      this.$q.notify({
        message: this.$t('field.awesome'),
        icon: 'thumb_up', color: 'accent',
        actions: [
          { dense: true, icon: 'fa-solid fa-xmark' , color: 'white', handler: () => { /* ... */ } }
        ]
      })
    },

    /**
     * On login
     */
    onLogin() {
      this.$auth.login(this.credential)
    }
  }
}
</script>
