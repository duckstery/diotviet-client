<template>
  <q-page class="bg-grey-3 tw-p-5">
    <div class="row tw-text-lg">
      <div class="col-6 tw-pr-2">
        <Button color="secondary" class="tw-w-full tw-h-[50px] tw-text-md" icon="fa-solid fa-qrcode"
                :label="$t('field.scan_identity')" @click="scan"/>
      </div>
      <div class="col-6 tw-pl-2">
        <Button color="warning" class="tw-w-full tw-h-[50px]" icon="fa-solid fa-rectangle-xmark"
                :label="$t('field.invalidate')" @click="invalidate"/>
      </div>
    </div>

    <div class="row tw-mt-5">
      <div class="col">
        <q-card flat bordered class="tw-mx-auto">

          <q-card-section>
            <q-banner v-if="identity.code === null || identity.code === ''"
                      :class="switchString(identity.code, 'bg-primary', 'bg-negative')">
              <template v-slot:avatar>
                <q-icon size="md" color="white"
                        :name="`fa-solid fa-circle-${switchString(identity.code, 'info', 'xmark')}`"/>
              </template>
              <span class="text-white">
                {{ switchString(identity.code, $t('message.please_scan'), $t('message.invalid_code')) }}
              </span>
              <template v-slot:action>
                <Button flat icon="fa-solid fa-qrcode" color="white" :label="$t('field.scan_identity')" @click="scan"/>
              </template>
            </q-banner>
            <div v-else>
              <span class="tw-text-3xl">{{ identity.code }}</span>
              <q-chip
                square dense size="md" class="tw-ml-2 tw-p-3"
                :color="identity.isValid ? 'positive' : 'warning'"
                :icon="`fa-solid fa-${identity.isValid ? 'shield' : 'radiation'}`"
              >
                {{ identity.isValid ? $t('field.valid') : $t('field.invalid') }}
              </q-chip>
            </div>
          </q-card-section>
          <q-separator inset/>
          <q-list>
            <q-item v-for="key in ['name', 'phoneNumber']" :key="key">
              <q-item-section avatar>
                <IconMage :src="`images/${$util.camelToSnake(key)}.png`"/>
              </q-item-section>

              <q-item-section class="col-2">
                <q-item-label class="tw-text-lg">{{ $t(`field.${$util.camelToSnake(key)}`) }}</q-item-label>
              </q-item-section>
              <q-separator vertical inset/>
              <q-item-section>
                <div class="tw-flex tw-ml-2">
                  <q-chip
                    square text-color="white" class="tw-text-lg"
                    :color="switchString(identity[key], 'warning', 'negative', 'positive')"
                    :icon="`fa-solid fa-circle-${switchString(identity[key], 'exclamation', 'xmark', 'info')}`"
                  >
                    {{ switchString(identity[key], $t('field.empty'), $t('field.error'), identity[key]) }}
                  </q-chip>
                </div>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card>
      </div>
    </div>

    <div v-if="identity.code" class="row tw-mt-5">
      <div class="col-12">
        <Button color="positive" class="tw-w-full tw-h-[50px]" icon="fa-solid fa-phone-volume"
                :label="$t('field.call_customer')" @click="call"/>
      </div>
    </div>
  </q-page>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import {reactive} from "vue";
import {axios, notify, util} from "src/boot";
import {useI18n} from "vue-i18n";
import {useCodeScanner} from "src/composables/useCodeScanner";
import IconMage from "components/General/Other/IconMage.vue";
import DisplayField from "components/General/Other/DisplayField.vue";

export default {
  name: 'InspectIdentityPage',

  components: {DisplayField, IconMage, Button},

  setup() {
    // Get i18n
    const $t = useI18n().t

    // Current code
    const identity = reactive({
      isValid: false,
      rawCode: '',
      code: '',
      name: '',
      phoneNumber: ''
    })
    // Switch string
    const switchString = (string, onEmpty, onNull, final = '') => {
      if (string === '') return onEmpty
      else if (string === null) return onNull
      else return final
    }

    // Handle scan
    const scan = useCodeScanner(barcode => {
      // Save code
      identity.rawCode = barcode.rawValue
      // Check if code is lest than 10 characters (This is ID instead)
      if (identity.rawCode.length <= 10) {
        // Assign
        Object.assign(identity, {
          isValid: false,
          rawCode: '',
          code: null,
          name: null,
          phoneNumber: null
        })

        return notify($t('message.invalid_identity'), 'warning')
      }

      // Send code to server for identification
      axios.get(`/ticket/view/${identity.rawCode}`, {loading: false})
        .then(res => Object.assign(identity, res.data.payload))
    })

    // Handle invalidate
    const invalidate = () => {
      // Check if code is empty
      if (util.isUnset(util.nullIfEmpty(identity.rawCode))) return notify($t('message.nothing_to_invalidate'), 'warning')
      // Check if code is invalid
      if (!identity.isValid) return notify($t('message.invalid_invalidate'), 'warning')

      // Save code for later usage
      const code = identity.rawCode
      axios.get(`/ticket/invalidate/${identity.rawCode}`)
        .then(() => {
          // Notify
          notify($t('message.success', {attr: $t('field.invalidate')}))
          // Check if nothing new is scanned
          if (identity.rawCode === code) {
            identity.isValid = false
          }
        })
    }

    return {
      identity: identity,
      switchString: switchString,
      scan: scan,
      invalidate: invalidate,
      call: () => window.open(`tel:${identity.phoneNumber}`)
    }
  }
}
</script>
