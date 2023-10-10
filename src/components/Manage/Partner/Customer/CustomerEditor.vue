<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[900px] virtual-scrollbar" style="max-width: 80vw;">
      <q-card-section>
        <div class="tw-text-lg tw-font-semibold">
          {{ mode === 'update' ? $t('field.update') : $t('field.create') }}
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row">
          <div class="tw-mt-3 col-12 col-md-6 tw-pr-5">
            <InputField
              v-for="key in ['code', 'name', 'groups']"
              :src="`/images/${key}.png`"
              :label="$t(`field.${key}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <TextField
                  v-if="['code', 'name'].includes(key)" v-bind="props" v-model="v$.input[key].$model"
                  compact input-class="tw-p-0"
                  :placeholder="key === 'code' ? $t('message.blank_for_auto') : ''"
                />
                <q-select
                  v-else v-bind="props" v-model="input[key]"
                  dense
                  map-options emit-value use-chips
                  :options="groups" option-label="name" option-value="id"
                  multiple
                />
              </template>
            </InputField>

            <InputField
              src="/images/gender.png"
              bottom
              :label="$t('field.gender')"
              :vuelidate="v$.input.isMale"
            >
              <template #default="props">
                <div class="tw-block" :class="props.class">
                  <div class="row">
                    <q-radio v-for="option in genderOptions"
                             v-model="v$.input.isMale.$model"
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
              v-for="key in ['birthday']"
              :src="`/images/${$util.camelToSnake(key)}.png`"
              :label="$t(`field.${$util.camelToSnake(key)}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <DatePicker v-model="v$.input.birthday.$model" v-bind="props"/>
              </template>
            </InputField>
          </div>

          <div class="tw-mt-3 col-12 col-md-6 tw-pr-5">
            <InputField
              v-for="key in ['address', 'phoneNumber', 'email', 'facebook']"
              :src="`/images/${$util.camelToSnake(key)}.png`"
              :label="$t(`field.${$util.camelToSnake(key)}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <TextField
                  v-bind="props" v-model="v$.input[key].$model"
                  compact input-class="tw-p-0"
                />
              </template>
            </InputField>
          </div>

          <div v-for="key in ['file', 'description']" class="tw-mt-3 col-12">
            <InputField
              horizontal
              :src="`/images/${key === 'file' ? 'image' : 'note'}.png`"
              :label="$t(`field.${key === 'file' ? 'image' : key}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <UploadMage
                  v-if="key === 'file'" v-model="v$.input[key].$model" v-bind="props"
                  :max-size="5242880"
                />
                <RichTextField
                  v-else v-model="v$.input[key].$model" v-bind="props"
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
import {useDialogPluginComponent} from 'quasar'
import {reactive} from 'vue'
import {useDialogEditor} from "src/composables/useDialogEditor";
import {required, numeric, email, maxLength} from '@vuelidate/validators'

import InputField from "components/General/Other/InputField.vue";
import Button from "components/General/Other/Button.vue";
import TextField from "components/General/Other/TextField.vue";
import UploadMage from "components/General/Other/UploadMage.vue";
import RichTextField from "components/General/Other/RichTextField.vue";
import DatePicker from "components/General/Other/DatePicker.vue";

export default {
  name: 'CustomerEditor',
  components: {DatePicker, RichTextField, UploadMage, TextField, InputField, Button},
  props: {
    // Editor mode: 'create', 'update', 'copy'
    mode: String,
    // Groups
    groups: Array,
    // Target item
    item: {
      type: Object,
      default: () => ({
        id: null,
        code: null,
        name: null,
        groups: [],
        gender: true,
        birthday: null,
        address: null,
        phoneNumber: null,
        email: null,
        facebook: null,
        description: null,
      }
        /*{
          id: 0,
          code: null,
          name: "Ahihi",
          groups: [],
          isMale: true,
          birthday: null,
          address: null,
          phoneNumber: '0123456789',
          email: null,
          facebook: null,
          src: null,
          description: null,
          createdAt: null,
          lastOrderAt: null,
          lastTransactionAt: null,
        }*/)
    }
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // Make a reactive input
    const input = reactive({...props.item})

    return {
      // Data
      input,
      ...useDialogEditor(input, props.mode),
    }
  },

  computed: {
    // Gender options
    genderOptions() {
      return [
        {label: this.$t('field.male'), val: true, color: 'primary', icon: 'fa-solid fa-mars'},
        {label: this.$t('field.female'), val: false, color: 'negative', icon: 'fa-solid fa-venus'}
      ]
    }
  },

  validations() {
    return {
      input: {
        code: {},
        name: {required},
        groups: {},
        isMale: {required},
        birthday: {},
        address: {},
        phoneNumber: {required, numeric, maxLength: maxLength(13)},
        email: {email},
        facebook: {},
        file: {},
        description: {},
      }
    }
  },
}
</script>
