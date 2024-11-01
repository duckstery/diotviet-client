<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[550px] tw-max-h-[600px] bg-grey-3">
      <q-card-section class="tw-p-3 tw-flex">
        <TextField v-model="debounce.search.value" icon="fa-solid fa-search" class="tw-border-amber-100 tw-flex-1"
                   :placeholder="$t('message.search_orders')"/>
        <Button class="tw-w-[41px] tw-ml-2" color="primary" :icon="getFilterIcon" :tooltip="getFilterTooltip"
                @click="isGroupByStatus = !isGroupByStatus"/>
      </q-card-section>
      <q-card-section class="tw-p-3 tw-pt-0">
        <div v-if="$_.isEmpty(search.data)" class="tw-text-center tw-text-lg tw-p-6 tw-text-gray-500">
          {{ $t('message.no_recent_searches') }}
        </div>
        <q-virtual-scroll
          v-else
          :items="search.data"
          #default="{ item, index }"
          class="tw-max-h-[492px] virtual-scrollbar"
        >
          <div v-if="item.isLabel" class="text-primary tw-text-lg tw-font-bold tw-mt-3 tw-h-[32px]">
            {{ item.label }}
          </div>
          <q-item
            v-else
            :key="index"
            :active="active.id === item.id"
            :active-class="`text-brand ` + ($q.dark.isActive ? '!tw-bg-emerald-700' : '!tw-bg-emerald-300')"
            clickable
            class="bg-brand-soft tw-group tw-my-2 tw-shadow tw-rounded tw-cursor-pointer hover:!tw-bg-blue-600 hover:!tw-text-white hover:!tw-border-none"
            @click="setActiveOrder(item)"
          >
            <q-item-section class="col-2 tw-font-bold">
              <q-item-label>{{ item.code }}</q-item-label>
              <q-item-label caption class="group-hover:!tw-text-gray-300">
                {{ $util.dateOnly(item.createdAt) }}
              </q-item-label>
            </q-item-section>
            <q-item-section class="col-1">
              <ConstantField short :value="item.status" target="status"/>
            </q-item-section>
            <q-item-section>
              <q-item-label lines="1">{{ item.customer }}</q-item-label>
              <q-item-label caption class="group-hover:!tw-text-gray-300">{{ item.phoneNumber }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-item-label class="tw-text-blue-500 tw-font-bold group-hover:!tw-text-white">
                {{ $util.formatMoney(item.paymentAmount) }}
                <q-icon name="fa-solid fa-tags" color="warning" size="small"/>
              </q-item-label>
            </q-item-section>
          </q-item>
        </q-virtual-scroll>
      </q-card-section>
      <template v-if="showToolbar">
        <q-card-section class="tw-p-0">
          <div class="bg-brand tw-p-1.5 tw-h-[50px] tw-flex"
               style="box-shadow: inset 0 1px 0 0 rgba(73,76,106,.5),0 -4px 8px 0 rgba(0,0,0,.2)">
            <Skeleton v-model="isActiveReady" width="80px">
              <div v-if="active" class="tw-my-auto tw-font-bold">
                <span>{{ active.code }}</span>:
                <span class="tw-text-blue-500">{{ $util.formatMoney(active.paymentAmount) }} &nbsp;</span>
                <q-icon name="fa-solid fa-tags" color="warning" size="small"/>
              </div>
            </Skeleton>

            <q-separator vertical spaced/>

            <q-space v-if="!isActiveReady"/>
            <Skeleton v-model="isActiveReady" width="270px">
              <OrderProcessorAction v-if="active" :active="active" @reload="reload"/>
            </Skeleton>
          </div>
        </q-card-section>
      </template>
    </q-card>
  </q-dialog>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import ConstantField from "components/General/Other/ConstantField.vue";
import InputField from "components/General/Other/InputField.vue";
import OrderProcessorAction from "components/Manage/Transaction/Order/OrderProcessorAction.vue";
import Skeleton from "components/General/Other/Skeleton.vue";

import {useDialogPluginComponent} from 'quasar'
import {computed, nextTick, onMounted, ref, toRef, watch, provide} from "vue";
import {useDialogEditor} from "src/composables/useDialogEditor";
import {useDebounceModel} from "src/composables/useDebounceModel";
import {useSimpleSearch} from "src/composables/useSimpleSearch";
import {useSimpleGrouper} from "src/composables/useSimpleGrouper";
import {env, util, constant, error} from "src/boot"
import {useI18n} from "vue-i18n";
import {watchOnce} from "@vueuse/core";

export default {
  name: 'OrderEditor',
  components: {ConstantField, Skeleton, OrderProcessorAction, InputField, Button, TextField},

  props: {
    // Selected id
    selectedCode: String,
    // Printer
    printer: Object
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // Get $t
    const $t = useI18n().t
    // Provide printer
    provide('printer', props.printer)

    // Group method flag
    const isGroupByStatus = ref(env.init("isGroupByStatus", false))
    // Initial group for simple search
    const grouper = computed(() => isGroupByStatus.value
      ? useSimpleGrouper('status', true, constant.statusCodeToString)
      : useSimpleGrouper('createdAt', false, util.dateOnly, util.dateOnly))
    // Filter icon
    const getFilterIcon = computed(() => isGroupByStatus.value ? 'fa-solid fa-circle' : 'fa-solid fa-calendar-days')
    // Filter tooltip
    const getFilterTooltip = computed(() => $t('message.group_by', {attr: $t(isGroupByStatus.value ? 'field.status' : 'field.date').toLowerCase()}))
    // Re-filter data when filter method is changed
    watch(isGroupByStatus, (value) => {
      // Trigger filter
      search.filter()
      // Save to env
      env.set("isGroupByStatus", value)
    })

    // Show toolbar flag
    const showToolbar = ref(false)
    // Loading active order flag
    const isActiveReady = ref(false)
    // Active order
    const active = ref(false)
    // Set active order
    const setActiveOrder = (item = null) => {
      // Show toolbar
      showToolbar.value = !util.isUnset(item)
      // Set active flag to false
      isActiveReady.value = false
      // Clear active order
      active.value = false

      // Set active order at the next of the next tick
      if (!util.isUnset(item)) {
        nextTick(async () => nextTick(() => {
          active.value = item
          isActiveReady.value = true
        })).catch(error.log)
      }
    }

    // Use simple search
    const search = useSimpleSearch('/order/query', true, grouper)
    // Setup debounce model
    const debounce = {search: useDebounceModel(toRef(search, 'query'))}
    // Reload search data
    const reload = () => {
      // Re-search data
      search.search()
      // Clear active
      setActiveOrder()
    }
    // Reset active state if query is changed
    watch(() => search.query, () => setActiveOrder())
    // If selectedCode is provided
    if (!util.isUnset(props.selectedCode)) {
      // Only watch once
      watchOnce(() => search.data, (value) => {
        // Pre-select the item that has code of [selectedCode]
        setActiveOrder(value.find(v => v.code === props.selectedCode) ?? null)
      })
      // Set initial value to trigger initial search if selectedCode is not null
      onMounted(() => search.query = `${props.selectedCode}`)
    }

    return {
      ...useDialogEditor(null, props.mode),
      isGroupByStatus, getFilterIcon, getFilterTooltip,
      active, isActiveReady, showToolbar, setActiveOrder,
      search, debounce, reload
    }
  },
}
</script>
