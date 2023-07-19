<template>
  <q-slide-transition :duration="150" @hide="state = 0">
    <div v-if="showPrompt && !$util.isUnset(action)" class="tw-my-auto">
      <q-chip
        square removable
        :text-color="chipTextColor" :color="action.color" :icon="action.icon" :label="action.verb"
        @remove="reset"
      />
    </div>
  </q-slide-transition>
  <q-space key="space"/>
  <q-slide-transition :duration="150" @hide="state = 2">
    <div v-if="showActions" class="tw-my-auto tw-flex">
      <template v-if="!isResolved">
        <Button key="process" flat color="info" icon="fa-solid fa-circle-dot"
                :label="$t('field.process')" @click="set(1)"/>
        <Button key="resolved" flat color="positive" icon="fa-solid fa-circle-check" class="tw-ml-3"
                :label="$t('field.resolve')" @click="set(2)"/>
      </template>
      <Button key="aborted" flat color="negative" icon="fa-solid fa-circle-stop" class="tw-ml-3"
              :label="$t('field.abort')" @click="handle"/>
    </div>
  </q-slide-transition>
  <q-slide-transition :duration="150" @hide="state = 0">
    <div v-if="showPrompt" class="tw-my-auto tw-flex">
      <TextField v-model="paymentAmount"
                 class="tw-w-32 tw-p-0" input-class="tw-font-semibold tw-p-0" mask="###,###,###,###" required
                 :placeholder="$t('field.payment_amount')"/>
      <Button class="tw-w-[41px] tw-ml-2" color="positive" icon="fa-solid fa-paper-plane"
              :tooltip="$t('field.confirm')" @click="handle"/>
      <Button class="tw-w-[41px] tw-ml-2" color="negative" icon="fa-solid fa-arrow-left"
              :tooltip="$t('field.cancel')" @click="reset"/>
    </div>
  </q-slide-transition>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import TextField from "components/General/Other/TextField.vue";
import IconMage from "components/General/Other/IconMage.vue";

import {useRangeControl} from "src/composables/useRangeControl";
import {ref, computed, nextTick} from "vue";
import {axios, constant, util, error, notify} from "src/boot";
import {Dark} from "quasar";
import {useI18n} from "vue-i18n";

export default {
  name: 'OrderEditorAction',

  components: {IconMage, TextField, Button},

  props: {
    // Active item's status
    active: Object,
  },

  emits: ['reload'],

  setup(props, context) {
    // Get $t
    const $t = useI18n().t

    // Payment amount
    const paymentAmount = ref(props.active.paymentAmount)
    // Use range control on payment amount
    useRangeControl(paymentAmount)

    // Active status
    const isResolved = computed(() => constant.isStatusResolved(props.active.status))
    const isAborted = computed(() => constant.isStatusAborted(props.active.status))

    // Transition state
    const state = ref(0)
    const hanging = () => nextTick(() => state.value = 1)

    // Active action
    const action = ref(null)
    // Set action
    const set = (code) => {
      action.value = constant.statuses()[code]
      // By transitioning to state 1, actions will be hidden
      hanging()
    }
    // Reset action
    const reset = () => {
      action.value = null
      // By transitioning back to state 1, prompt will also be hidden
      hanging()
      nextTick(() => {
        // Avoid data glitching when q-slide is transitioning
        paymentAmount.value = props.active.paymentAmount
      })
    }
    // Handle action
    const handle = () => {
      // Check if action is null
      if (util.isUnset(action.value)) {
        action.value = constant.statuses()[3]
      }

      util.promptConfirm($t('message.action_on_order', {attr: action.value.verb}))
        .onOk(() => {
          // Send a request to patch order
          axios.patch('/order/patch', {
            ids: [props.active.id],
            versions: [props.active.version],
            option: action.value.id,
            amount: paymentAmount.value,
          })
            .then(() => {
              notify($t('message.success', {attr: action.value.verb}), 'positive')
              // Reload Editor
              context.emit('reload')
            })
            .catch(error.any)
        })
    }

    return {
      /** Data **/
      action, paymentAmount,
      /** Manipulate action **/
      set, reset, handle,
      /** Active status **/
      isResolved, isAborted,
      /** Style **/
      chipTextColor: computed(() => Dark.isActive ? 'black' : 'white'),
      /** Transition control **/
      state,
      // Show actions if active.status is not aborted and transition is at state 0 (actioning)
      showActions: computed(() => !isAborted.value && state.value === 0),
      // Show prompt if transition is at state 3 (prompting)
      showPrompt: computed(() => state.value === 2)
    }
  },
}
</script>
