<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[550px] tw-max-h-[600px] bg-grey-3">
      <q-card-section>
        <div class="tw-text-lg tw-font-semibold">
          {{ $t('field.change_password') }}
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row">
          <div class="tw-mt-3 col-12 tw-pr-5">
            <InputField
              v-for="key in ['password', 'newPassword', 'confirmPassword']"
              :src="`/images/${key === 'password' ? '' : 'new_'}pass.png`"
              :label="$t(`field.${$util.camelToSnake(key)}`)"
              :vuelidate="v$.input[key]"
              :space="$q.screen.gt.xs"
              :horizontal="$q.screen.lt.sm"
            >
              <template #default="props">
                <TextField v-bind="props" v-model="v$.input[key].$model" type="password" :class="fieldClasses"/>
              </template>
            </InputField>
          </div>
        </div>
      </q-card-section>
      <q-card-actions align="right">
        <Button :label="$t('field.confirm')" icon="fa-solid fa-check"
                stretch color="positive" class="tw-ml-2" no-caps @click="onConfirm"/>
        <Button :label="$t('field.cancel')" icon="fa-solid fa-xmark"
                stretch color="negative" class="tw-ml-2" no-caps @click="onCancel"/>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import InputField from "components/General/Other/InputField.vue";
import Button from "components/General/Other/Button.vue";
import IconMage from "components/General/Other/IconMage.vue";

import {useDialogPluginComponent, Screen} from 'quasar'
import {useDialogEditor} from "src/composables/useDialogEditor";
import {computed, reactive} from "vue";
import {required} from "@vuelidate/validators";
import {auth, error, notify} from "src/boot";
import {useI18n} from "vue-i18n";

export default {
  name: 'PasswordPanel',

  components: {IconMage, Button, InputField, TextField},

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup() {
    // i18n
    const $t = useI18n().t

    // Input
    const input = reactive({
      password: '',
      newPassword: '',
      confirmPassword: ''
    })
    // Use key enforcer
    const resources = useDialogEditor(input, 'change_password')

    // On confirm
    const onConfirm = async () => {
      // Validate file
      if (await resources.v$.value.$validate()) {
        // Send request
        auth.changePassword(input.password, input.newPassword)
          .then(() => {
            // Notify
            notify($t("message.success", {attr: $t(`field.change_password`)}))
            // Close
            resources.ok()
          })
          .catch(error.switch({
            410: resources.ok,
            422: [{v$: resources.v$.value}, 'input'],
            default: error.any
          }))
      } else {
        // Notify about invalid
        notify($t("message.invalid_input"), 'negative')
      }
    }

    return {
      input: input,
      ...resources,
      // On confirm
      onConfirm: onConfirm,
      // Field classes
      fieldClasses: computed(() => ({
        'tw-w-3/5': Screen.gt.xs,
        'tw-mt-1': true,
      }))
    }
  },

  validations() {
    return {
      input: {
        password: {required},
        newPassword: {
          required,
          match_password: (value) => value === this.input.confirmPassword
        },
        confirmPassword: {
          required,
          match_password: (value) => value === this.input.newPassword
        },
      }
    }
  },
}
</script>
