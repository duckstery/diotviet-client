<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[550px] tw-max-h-[600px] bg-grey-3">
      <q-card-section>
        <div class="tw-text-lg tw-font-semibold">
          {{ $t('field.create_transaction') }}
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row">
          <div class="tw-mt-3 col-12 tw-pr-5">
            <InputField
                src="images/setup.png"
                bottom
                :label="$t('field.type')"
                :vuelidate="v$.input.type"
            >
              <template #default="props">
                <div class="tw-block" :class="props.class">
                  <div class="row">
                    <q-radio v-for="option in typeOptions"
                             v-model="v$.input.type.$model"
                             unchecked-icon="panorama_fish_eye"
                             keep-color

                             :val="option.val"
                             :label="option.label"
                             :color="option.color"
                             :checked-icon="option.icon"
                    />
                  </div>
                  <div class="row">
                    <div :class="props['bottom-class']">{{ props['error-message'] }}</div>
                  </div>
                </div>
              </template>
            </InputField>
            <InputField
                v-for="key in ['amount', 'reason']"
                :src="`images/${key}.png`"
                :label="$t(`field.${key}`)"
                :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <TextField
                    v-bind="props" v-model="v$.input[key].$model" compact input-class="tw-p-0" required
                    :mask="key === 'amount' ? '###,###,###,###' : ''"
                    :placeholder="key === 'code' ? $t('message.blank_for_auto') : ''"
                    @blur="onResetAmount(key)"
                />
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

import {useDialogPluginComponent} from 'quasar'
import {useDialogEditor} from "src/composables/useDialogEditor";
import {computed, reactive, toRef} from "vue";
import {required} from "@vuelidate/validators";
import {useKeyEnforcer} from "src/composables/useKeyEnforcer";
import {useRangeControl} from "src/composables/useRangeControl";
import {useI18n} from "vue-i18n";

export default {
  name: 'TransactionEditor',

  components: {Button, InputField, TextField},

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // i18n
    const $t = useI18n().t
    // Use key enforcer
    useKeyEnforcer("transaction")

    // Input
    const input = reactive({
      type: 1,
      amount: '1000',
      reason: ''
    })
    // Use range control on 'amount'
    useRangeControl(toRef(input, 'amount'), 999999999999, 0)

    return {
      typeOptions: computed(() => [
        {label: $t('field.collect'), val: 1, color: 'positive', icon: 'fa-solid fa-plus'},
        {label: $t('field.spend'), val: -1, color: 'negative', icon: 'fa-solid fa-minus'}
      ]),
      input: input,
      onResetAmount: (key) => {
        if (key === 'amount' && input.amount.length < 4) input.amount = '1000'
      },
      ...useDialogEditor(input, props.mode),
    }
  },

  validations() {
    return {
      input: {
        type: {required},
        amount: {required},
        reason: {required},
      }
    }
  },
}
</script>
