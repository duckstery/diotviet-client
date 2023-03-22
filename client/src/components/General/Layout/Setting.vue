<template>
  <Button flat icon="settings" color="white" :tooltip="$t('field.settings')">
    <q-popup-proxy class="tw-pb-3">
      <!--  -->
      <q-banner class="tw-mt-1">
        <template v-slot:avatar>
          <q-avatar size="32px" square>
            <img src="images/translation.png">
          </q-avatar>
        </template>
        <div class="tw-font-medium">{{ $t('field.language') }}</div>
      </q-banner>
      <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
        <Button src="images/en_US.png" color="primary" :flat="!isEnglish" stretch :label="$t('field.en')"
                class="tw-w-[140px]" @click="onChangeEnv('language', 'en')"/>
        <Button src="images/vi_VN.png" color="primary" :flat="isEnglish" stretch :label="$t('field.vi')"
                class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('language', 'vi')"/>
      </div>

      <q-banner class="tw-mt-1">
        <template v-slot:avatar>
          <q-avatar size="32px" square>
            <img src="images/dark-mode.png">
          </q-avatar>
        </template>
        <div class="tw-font-medium">{{ $t('field.display_mode') }}</div>
      </q-banner>
      <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
        <Button src="images/light.png" color="primary" :flat="!isLight" stretch :label="$t('field.light_mode')"
                class="tw-w-[140px]" @click="onChangeEnv('display', 'light')"/>
        <Button src="images/dark.png" color="primary" :flat="isLight" stretch :label="$t('field.dark_mode')"
                class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('display', 'dark')"/>
      </div>
    </q-popup-proxy>
  </Button>
</template>

<script>
import {env} from "boot/env";

import Button from "components/General/Other/Button.vue";

export default {
  name: 'Setting',

  components: {Button},

  data: () => ({
    language: env.get("language") ?? 'en', // Default is English
    display: env.get("display") ?? 'light', // Default is light
  }),

  computed: {
    // Check if language is English. Why? cuz it is default value
    isEnglish() {
      return this.language === 'en'
    },
    // Check if display is Light. Why? cuz it is default value
    isLight() {
      return this.display === 'light'
    },
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
    }
  },

  watch: {
    // Handler for changing language
    language: {
      immediate: true,
      handler(value) {
        this.$i18n.locale = value
      }
    },
    // Handler for changing display
    isLight: {
      immediate: true,
      handler(value) {
        this.$q.dark.set(!value)
      }
    }
  },
}
</script>
