<template>
  <q-card flat class="tw-h-full" :style="`width: ${width}px`">
    <q-card-section>
      <Skeleton v-model="isReady" height="28px" width="100px">
        <div class="tw-text-lg tw-font-semibold text-primary">
          {{ detail.title ?? 'Title' }}
        </div>
      </Skeleton>
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
              :src="`/images/equality.png`"
              :label="$t(`field.gender`)"
            >
              <q-icon :name="`fa-solid fa-${detail.isMale ? 'mars' : 'venus'}`"
                      :color="`${detail.isMale ? 'primary' : 'negative'}`"
                      size="xs"
                      class="tw-my-auto tw-pt-3 tw-ml-3"/>
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
          <template v-for="key in ['createdAt', 'lastTransactionAt']">
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
                stretch color="info" class="tw-ml-2" no-caps @click="request('history')"/>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button :label="$t('field.edit')" icon="fa-solid fa-pen-to-square"
                stretch color="primary" class="tw-ml-2" no-caps @click="request('update', this.detail)"/>
        <Button :label="$t('field.copy')" icon="fa-solid fa-copy"
                stretch color="positive" class="tw-ml-2" no-caps @click="request('copy', this.detail)"/>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button :label="$t('field.delete')" icon="fa-solid fa-trash"
                stretch color="negative" class="tw-ml-2" no-caps @click="request('delete', [this.getItemId])"/>
      </Skeleton>
    </q-card-section>
  </q-card>
</template>

<script>
import DisplayField from "components/General/Other/DisplayField.vue";
import Button from "components/General/Other/Button.vue";
import Skeleton from "components/General/Other/Skeleton.vue";
import {usePageRowDetail} from "src/composables/usePageRowDetail";
import {toRefs} from "vue";
import LabelField from "components/General/Other/LabelField.vue";

export default {
  name: 'CustomerDetail',

  components: {LabelField, Skeleton, Button, DisplayField},

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
      ...usePageRowDetail("customer", toRefs(props), context)
    }
  },

  methods: {
    /**
     * On interact
     *
     * @param mode
     * @param data
     */
    onInteract(mode, data) {
      console.warn(mode)
      if (mode === 'phoneNumber') {
        // Call number
        window.open(`tel:${data}`)
      } else if (mode === 'email') {
        // Open Gmail interface to compose email
        window.open(`https://mail.google.com/mail/?view=cm&fs=1&to=${data}`)
      } else if (mode === 'facebook') {
        // Open facebook link
        window.open(`https://${data}`)
      } else if (mode === 'address') {
        // Open Google Map link
        window.open(`https://www.google.com/maps/place/${data}`)
      }
    },
  }
}
</script>
