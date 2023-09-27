<template>
  <q-timeline :layout="layout">
    <template v-for="group in entriesByDate" :key="group.date">
      <q-timeline-entry v-if="group.heading" heading>
        <Anchor :alias="`e${group.yearMonth}`" :content="printYearMonth(group.yearMonth)"/>
      </q-timeline-entry>
      <q-timeline-entry color="primary" icon="fa-solid fa-location-dot">
        <template #title>
          <hr class="tw-border-dashed tw-line-height tw-leading-none text-primary">
        </template>

        <template #subtitle>
          <Anchor :alias="`e${group.date}`" :content="group.date" reverse size="xs"/>
        </template>

        <template #default>
          <SimpleDisplayField icon="fa-solid fa-arrow-trend-up" color="positive" label-class="tw-w-[128px]" dense
                              :label="text.collect" :content="printAmount(group.collectedAmount, group.isEstimating)"/>
          <SimpleDisplayField icon="fa-solid fa-arrow-trend-down" color="negative" label-class="tw-w-[128px]" dense
                              :label="text.spend" :content="printAmount(group.spentAmount, group.isEstimating)"/>

          <q-timeline :layout="layout" class="tw-mt-4">
            <q-timeline-entry
                v-for="entry in group.entries"
                :key="entry.time"
                :title="$util.formatMoney(entry.amount)"
                :subtitle="entry.time.substring(0, 8)"
                :icon="`fa-solid ${entry.isCollect ? 'fa-plus' : 'fa-minus'}`"
                :color="entry.isCollect ? 'positive' : 'negative'"
            >
              <template v-if="!$util.isUnset(entry.order)">
                <SimpleDisplayField
                    icon="fa-solid fa-receipt" label-class="tw-w-[128px]" dense
                    :label="text.order"
                >
                  <div
                      class="text-primary tw-cursor-pointer hover:!tw-underline tw-underline-offset-2"
                      @click="goToOrder(entry.order.code)"
                  >
                    {{ entry.order.code }}
                  </div>
                </SimpleDisplayField>
                <SimpleDisplayField icon="fa-solid fa-user-tag" label-class="tw-w-[128px]" dense
                                    :label="text.customer" :content="entry.order.customerName"/>
              </template>
              <SimpleDisplayField icon="fa-solid fa-lightbulb" label-class="tw-w-[128px]" dense
                                  :label="text.reason" :content="entry.reason"/>
            </q-timeline-entry>

            <q-timeline-entry color="orange" icon="fa-solid fa-check-double" :subtitle="$t('field.end')"/>
          </q-timeline>
        </template>
      </q-timeline-entry>
    </template>

    <q-timeline-entry :color="lastEntry.color" :icon="lastEntry.icon" :subtitle="lastEntry.subtitle">
      <template #title>
        <Button v-if="lastEntry.isLast" stretch color="orange" :label="$t('field.to_top')"
                @click="$router.push({hash: `#${entriesByDate[0].yearMonth}`})"/>
        <Button v-else stretch color="info" :label="$t('field.continue')"
                @click="$emit('continue', timeline.current + 1)"/>
      </template>
    </q-timeline-entry>
  </q-timeline>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import SimpleDisplayField from "components/General/Other/SimpleDisplayField.vue";

import {computed, toRef} from "vue";
import {useI18n} from "vue-i18n";
import {useRouter} from "vue-router";
import {useTimeline} from "src/composables/useTimeline";
import {util, dayjs} from "src/boot";
import Anchor from "components/General/Other/Anchor.vue";

export default {
  name: "Timeline",

  components: {Anchor, SimpleDisplayField, Button},

  props: {
    // Timeline's entries. Accept Spring page object
    value: Object,
    // Layout
    layout: {
      type: String,
      default: 'comfortable'
    }
  },

  emits: ['continue'],

  setup(props) {
    // i18n
    const $t = useI18n().t
    // Router
    const router = useRouter()

    // Go to Order's detail
    const goToOrder = (code) => router.push({name: 'Transaction.Order', query: {q: code}})

    // Print
    const printAmount = (value, isEstimating) => util.formatMoney(value) + (isEstimating ? ` (${$t('field.estimate')})` : '')
    const printYearMonth = (yearMonth) => {
      // Parse yearMonth to dayjs
      const instance = dayjs(yearMonth, 'YYYY-MM')

      return $t(`field.month_${instance.get('month') + 1}`) + ', ' + instance.get('year')
    }

    // Use timeline resources
    const useTimelineResources = useTimeline(toRef(props, 'value'))

    return {
      // Timeline's resources
      ...useTimelineResources,
      // Methods
      goToOrder: goToOrder, printAmount: printAmount, printYearMonth: printYearMonth,
      // Translated text
      text: computed(() => ({
        collect: $t('field.collect'),
        spend: $t('field.spend'),
        order: $t('field.order'),
        customer: $t('field.customer'),
        reason: $t('field.reason'),
      })),
      // Last entry
      lastEntry: computed(() => ({
        isLast: useTimelineResources.timeline.last,
        color: useTimelineResources.timeline.last ? 'orange' : 'info',
        icon: 'fa-solid ' + (useTimelineResources.timeline.last ? 'fa-up-long' : 'fa-square-caret-down'),
        subtitle: useTimelineResources.timeline.last ? $t('field.end') : ($t('field.more') + ' ...')
      }))
    }
  }
}
</script>

<style scoped>
:deep(.q-timeline__subtitle) {
  width: 13%;
}
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
