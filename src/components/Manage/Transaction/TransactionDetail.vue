<template>
  <q-card flat class="tw-h-full" :style="`width: ${width}px`">
    <q-card-section>
      <Skeleton v-model="isReady" height="28px" width="100px">
        <div class="tw-text-lg tw-font-semibold text-primary">
          {{ $t('field.information') }}
        </div>
      </Skeleton>
      <div class="row">
        <!-- Info -->
        <div class="tw-mt-3 col-12">
          <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
            <DisplayField
              custom
              src="/images/setup.png"
              :label="$t('field.type')"
            >
              <ConstantField :value="detail.type" target="type" class="tw-my-auto tw-pt-3 tw-ml-3"/>
            </DisplayField>
          </Skeleton>
          <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
            <DisplayField
              mask="###,###,###,###"
              src="/images/amount.png"
              :modelValue="detail.amount"
              :label="$t('field.amount')"
            />
          </Skeleton>
          <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
            <DisplayField
              src="/images/created_at.png"
              :modelValue="detail.createdAt"
              :label="$t('field.created_at')"
            />
          </Skeleton>
          <Skeleton v-model="isReady" height="300px">
            <DisplayField
              :modelValue="detail.reason"
              textarea
              horizontal
              :src="'/images/note.png'"
              :label="$t(`field.note`)"
              textarea-height="185"
              textarea-length="100"
            />
          </Skeleton>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import DisplayField from "components/General/Other/DisplayField.vue";
import Skeleton from "components/General/Other/Skeleton.vue";

import {toRefs} from "vue";
import {usePageRowDetail} from "src/composables/usePageRowDetail";
import ConstantField from "components/General/Other/ConstantField.vue";

export default {
  name: 'TransactionDetail',

  components: {ConstantField, Skeleton, DisplayField},

  props: {
    // Product props
    item: {
      type: Object,
      default: () => ({
        items: []
      })
    },
    // Width
    width: {
      type: Number,
      default: 300
    },
    // Active status
    active: {
      type: Boolean,
      default: false
    }
  },

  setup(props, context) {
    // Use page row detail
    return {
      ...usePageRowDetail(toRefs(props), context),
    }
  }
}
</script>
