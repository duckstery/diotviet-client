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
          <div v-if="item.isLabel" class="text-primary tw-text-lg tw-font-bold tw-mt-3 tw-h-[32px]">{{ item.label }}</div>
          <q-item
            v-else
            :key="index"
            :active="active.id === item.id"
            :active-class="`text-brand ` + ($q.dark.isActive ? '!tw-bg-emerald-700' : '!tw-bg-emerald-300')"
            clickable
            class="bg-brand-soft tw-group tw-my-2 tw-shadow tw-rounded tw-cursor-pointer hover:!tw-bg-blue-600 hover:!tw-text-white hover:!tw-border-none"
            @click="active = item"
          >
            <q-item-section class="col-2 tw-font-bold">
              <q-item-label>{{ item.code }}</q-item-label>
              <q-item-label caption class="group-hover:!tw-text-gray-300">
                {{ $util.dateOnly(item.createdAt) }}
              </q-item-label>
            </q-item-section>
            <q-item-section class="col-1">
              <OrderStatus short :value="item.status"/>
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
      <template v-if="active">
        <q-card-section class="tw-p-0">
          <div class="bg-brand tw-p-1.5 tw-h-[44px] tw-flex"
               style="box-shadow: inset 0 1px 0 0 rgba(73,76,106,.5),0 -4px 8px 0 rgba(0,0,0,.2)">
            <div class="tw-my-auto tw-font-bold">
              <span>{{ active.code }}</span>:
              <span class="tw-text-blue-500">{{ $util.formatMoney(active.paymentAmount) }} &nbsp;</span>
              <q-icon name="fa-solid fa-tags" color="warning" size="small"/>
            </div>
            <q-space/>
            <template v-if="!$constant.isStatusAborted(active.status)">
              <template v-if="!$constant.isStatusResolved(active.status)">
                <Button flat color="info" icon="fa-solid fa-circle-dot"
                        :label="$t('field.process')"/>
                <Button flat color="positive" icon="fa-solid fa-circle-check" class="tw-ml-3"
                        :label="$t('field.resolve')"/>
              </template>
              <Button flat color="negative" icon="fa-solid fa-circle-stop" class="tw-ml-3"
                      :label="$t('field.abort')"/>
            </template>
          </div>
        </q-card-section>
      </template>
    </q-card>
  </q-dialog>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import OrderStatus from "components/Manage/Constant/OrderStatus.vue";

import {useDialogPluginComponent} from 'quasar'
import {computed, ref, toRef, watch} from "vue";
import {useDialogEditor} from "src/composables/useDialogEditor";
import {useDebounceModel} from "src/composables/useDebounceModel";
import {useSimpleSearch} from "src/composables/useSimpleSearch";
import {useSimpleGrouper} from "src/composables/useSimpleGrouper";
import {env, util, constant} from "src/boot"

export default {
  name: 'OrderEditor',
  components: {OrderStatus, Button, TextField},

  props: {
    // Selected id
    selectedId: String,
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // Is filter by status flag
    const isGroupByStatus = ref(env.init("isGroupByStatus", false))
    // Filter for criteria
    const grouper = computed(() => isGroupByStatus.value
      ? useSimpleGrouper('status', true, constant.statusCodeToString)
      : useSimpleGrouper('createdAt', false, util.dateOnly, util.dateOnly))

    // Use simple search
    const search = useSimpleSearch('/order/query', true, grouper)
    // Setup debounce model
    const debounce = {search: useDebounceModel(toRef(search, 'query'))}
    // Active id
    const active = ref(false)

    // Reset active state if query is changed
    watch(() => search.query, () => active.value = false)
    // Re-filter data when filter method is changed
    watch(isGroupByStatus, (value) => {
      // Trigger filter
      search.filter()
      // Save to env
      env.set("isGroupByStatus", value)
    })

    return {
      ...useDialogEditor(null, props.mode),
      active,
      search,
      debounce,
      isGroupByStatus
    }
  },

  computed: {
    // Filter icon
    getFilterIcon() {
      return this.isGroupByStatus ? 'fa-solid fa-circle' : 'fa-solid fa-calendar-days'
    },
    // Filter tooltip
    getFilterTooltip() {
      const key = this.isGroupByStatus ? 'field.status' : 'field.date'
      return this.$t('message.group_by', {attr: this.$t(key).toLowerCase()})
    },
  },
}
</script>
