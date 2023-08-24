<template>
  <Button v-bind="$attrs" icon="settings" color="white" :tooltip="$t('field.settings')">
    <q-popup-proxy class="tw-pb-3">
      <!--  -->
      <q-banner class="tw-mt-1">
        <template v-slot:avatar>
          <IconMage src="/images/translation.png"/>
        </template>
        <div class="tw-font-medium">{{ $t('field.language') }}</div>
      </q-banner>
      <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
        <Button src="/images/en_US.png" color="primary" :flat="!isEnglish" stretch :label="$t('field.en')"
                class="tw-w-[140px]" @click="onChangeEnv('language', 'en')"/>
        <Button src="/images/vi_VN.png" color="primary" :flat="isEnglish" stretch :label="$t('field.vi')"
                class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('language', 'vi')"/>
      </div>

      <q-banner class="tw-mt-1">
        <template v-slot:avatar>
          <IconMage src="/images/dark-mode.png"/>
        </template>
        <div class="tw-font-medium">{{ $t('field.display_mode') }}</div>
      </q-banner>
      <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
        <Button src="/images/light.png" color="primary" :flat="!isLight" stretch :label="$t('field.light_mode')"
                class="tw-w-[140px]" @click="onChangeEnv('display', 'light')"/>
        <Button src="/images/dark.png" color="primary" :flat="isLight" stretch :label="$t('field.dark_mode')"
                class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('display', 'dark')"/>
      </div>

      <q-banner class="tw-mt-1">
        <template v-slot:avatar>
          <IconMage src="/images/optimize.png"/>
        </template>
        <div class="tw-font-medium">{{ $t('field.optimize') }}</div>
      </q-banner>
      <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
        <Button src="/images/visual.png" color="primary" :flat="!isOptimizeVisual" stretch :label="$t('field.visual')"
                class="tw-w-[140px]" @click="onChangeEnv('optimize', 'visual')"/>
        <Button src="/images/speed.png" color="primary" :flat="isOptimizeVisual" stretch :label="$t('field.speed')"
                class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('optimize', 'speed')"/>
      </div>
      <q-banner class="tw-mt-1">
        <template v-slot:avatar>
          <IconMage src="/images/print.png"/>
        </template>
        <div class="tw-font-medium">{{ $t('field.print') }}</div>
      </q-banner>
      <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
        <Button src="/images/setup.png" color="primary" stretch flat :label="$t('field.setup')"
                class="tw-w-full" @click="onSetup('print')"/>
      </div>
    </q-popup-proxy>
  </Button>
</template>

<script>
import {env} from "boot/env";

import Button from "components/General/Other/Button.vue";
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: 'Setting',

  components: {IconMage, Button},

  data: () => ({
    language: env.get("language") ?? 'en', // Default is English
    display: env.get("display") ?? 'light', // Default is light
    optimize: env.get("optimize") ?? 'visual, // Default is visual'
  }),

  computed: {
    // Check if language is English. Why? cuz it is default value
    isEnglish() {
      return this.language !== 'vi'
    },
    // Check if display is Light. Why? cuz it is default value
    isLight() {
      return this.display !== 'dark'
    },
    // Check if optimize for visual. Why? cuz it is default value
    isOptimizeVisual() {
      return this.optimize !== 'speed'
    }
  },

  methods: {
    /**
     * On change env
     *
     * @param key
     * @param value
     */
    onChangeEnv(key, value) {
      // Set local value
      this[key] = value
      // Set to LocalStorage
      this.$env.set(key, value)
    },

    /**
     * To setting page
     *
     * @param key
     */
    onSetup(key) {
      this.$router.push({name: `Setting.${this.$_.capitalize(key)}`})
    }
  },

  watch: {
    // Handler for changing language
    language(value) {
      this.$i18n.locale = value
    },
    // Handler for changing display
    isLight(value) {
      this.$q.dark.set(!value)
    },
    // Handler for changing optimize aspect
    isOptimizeVisual() {
      window.location.reload()
    }
  },
}
</script>
