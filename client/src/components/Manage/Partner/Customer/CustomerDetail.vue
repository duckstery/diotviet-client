<template>
  <q-card flat class="tw-h-full" :style="`width: ${width}px`">
    <q-card-section>
      <Skeleton v-model="isReady" height="28px" width="100px">
        <div class="tw-text-lg tw-font-semibold text-primary">
          {{ detail.name ?? 'Title' }}
        </div>
      </Skeleton>
      <div class="tw-mt-2 tw-flex tw-text-sm">
        <Skeleton v-model="isReady" height="20px" width="200px">
          <div class="tw-flex tw-mr-3">
            <div class="tw-ml-2">{{ $t('field.point') }}: {{ detail.point ?? 0 }}</div>
          </div>
        </Skeleton>
      </div>
      <div class="row">
        <!-- Image -->
        <div class="tw-mt-3 col-12 col-lg-4 col-md-6 tw-px-1.5">
          <Skeleton v-model="isReady" height="300px">
            <q-img
              no-native-menu
              no-spinner
              no-transition
              width="300"
              height="300"
              style="max-width: 300px; max-height: 300px"
              :src="detail.src"
            />
          </Skeleton>
        </div>
        <!-- Primary info -->
        <div class="tw-mt-3 col-12 col-lg-4 col-md-6 tw-px-1.5">
          <template v-for="key in ['code', 'category', 'groups']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                :modelValue="detail[key]"
                :src="`/images/${key}.png`"
                :label="$t(`field.${key}`)"
              />
            </Skeleton>
          </template>

          <div class="tw-mt-5"/>
          <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
            <DisplayField
              custom
              :src="`/images/gender.png`"
              :label="$t(`field.gender`)"
            >
              <ConstantField :value="detail.isMale" target="gender" class="tw-my-auto tw-pt-3 tw-ml-3"/>
            </DisplayField>
          </Skeleton>

          <template v-for="key in ['birthday', 'address', 'phoneNumber', 'email', 'facebook']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                :modelValue="detail[key]"
                :src="`/images/${$util.camelToSnake(key)}.png`"
                :label="$t(`field.${$util.camelToSnake(key)}`)"
                :interactive="key !== 'birthday'"
                @interact="onInteract(key, detail[key])"
              />
            </Skeleton>
          </template>
        </div>
        <!-- Secondary info -->
        <div class="tw-mt-3 col-12 col-lg-4 tw-px-1.5">
          <Skeleton v-model="isReady" height="300px">
            <DisplayField
              :modelValue="this.detail.description"
              textarea
              horizontal
              :src="'/images/note.png'"
              :label="$t(`field.description`)"
              textarea-height="244"
              textarea-length="100"
            />
          </Skeleton>

          <template v-for="key in ['createdAt', 'lastOrderAt', 'lastTransactionAt']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                :modelValue="detail[key]"
                :src="`/images/${$util.camelToSnake(key)}.png`"
                :label="$t(`field.${$util.camelToSnake(key)}`)"
              />
            </Skeleton>
          </template>
        </div>
      </div>
    </q-card-section>
    <q-card-section class="tw-flex tw-pt-0">
      <q-space/>
      <Skeleton v-model="isReady" height="40px" width="300px" skeleton-class="tw-w-full">
        <Button :label="$t('field.history')" icon="fa-solid fa-clock-rotate-left"
                stretch color="secondary" class="tw-ml-2" no-caps @click="request('history')"/>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button :label="$t('field.edit')" icon="fa-solid fa-pen-to-square"
                stretch color="primary" class="tw-ml-2" no-caps @click="request('update', this.detail)"/>
        <Button :label="$t('field.copy')" icon="fa-solid fa-copy"
                stretch color="positive" class="tw-ml-2" no-caps @click="request('copy', this.detail)"/>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button :label="$t('field.delete')" icon="fa-solid fa-trash"
                stretch color="negative" class="tw-ml-2" no-caps @click="remove"/>
      </Skeleton>
    </q-card-section>
  </q-card>
</template>

<script>
import DisplayField from "components/General/Other/DisplayField.vue";
import Button from "components/General/Other/Button.vue";
import Skeleton from "components/General/Other/Skeleton.vue";
import LabelField from "components/General/Other/LabelField.vue";
import ConstantField from "components/General/Other/ConstantField.vue";

import {toRefs} from "vue";
import {usePageRowDetail} from "src/composables/usePageRowDetail";
import {useInteractiveField} from "src/composables/useInteractiveField";

export default {
  name: 'CustomerDetail',

  components: {ConstantField, LabelField, Skeleton, Button, DisplayField},

  props: {
    // Customer props
    item: {
      type: Object,
      default: () => ({})
    },
    // Width
    width: {
      type: Number,
      default: 500
    },
    // Active status
    active: {
      type: Boolean,
      default: false
    }
  },

  emits: ['request'],

  setup(props, context) {
    return {
      ...usePageRowDetail(toRefs(props), context),
      ...useInteractiveField()
    }
  },
}
</script>
