<template>
  <q-card class="tw-p-1 hover:tw-border-sky-400 tw-border-transparent tw-border-solid tw-cursor-pointer" flat>
    <q-card-section class="tw-p-0" :horizontal="!visualize">
      <!-- Image (visual) section -->
      <q-img
        ratio="1"
        :width="size"
        :height="size"
        :src="value.src"
        :class="`tw-min-h-[${size}] tw-min-w-[${size}] tw-max-h-[${size}] tw-max-w-[${size}] tw-rounded-md`"
      >
        <!-- Price section -->
        <div v-if="visualize" class="tw-absolute tw-left-0 tw-bottom-0 tw-p-0 tw-font-medium">
          <q-card-section className="tw-p-0 tw-font-medium">
            {{ priceText }} <q-icon v-if="value.discount" name="fa-solid fa-tags" size="24" color="warning"/>
          </q-card-section>
        </div>
      </q-img>

      <!-- Content section -->
      <q-card-section class="tw-p-0 tw-ml-2">
        <q-card-section class="tw-p-0 tw-overflow-hidden tw-text-ellipsis tw-line-clamp-2 tw-max-w-[135px]">
          {{ value.title }}
          <q-tooltip class="tw-text-base">{{ value.title }}</q-tooltip>
        </q-card-section>
        <div v-if="!visualize" class="text-primary tw-mb-2.5">
          <q-card-section class="tw-p-0 tw-font-medium tw-absolute tw-bottom-0">
            {{ priceText }} <q-icon v-if="value.discount" name="fa-solid fa-tags" size="24" color="warning"/>
          </q-card-section>
        </div>
      </q-card-section>

    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'SampleItem',

  props: {
    // Determine if item should be visualized
    visualize: {
      type: Boolean,
      default: false
    },
    // Item's value
    value: {
      type: Object,
      default: () => ({
        id: 1,
        code: '001',
        title: 'Title of cdddd ddddd ddddd ddd dddddddddd ddddddddd'.toUpperCase(),
        originalPrice: '50000',
        discount: '10',
        discountUnit: '%',
        actualPrice: '45000',
        measureUnit: 'Kg',
        src: 'https://cdn.quasar.dev/img/parallax2.jpg'
      })
    }
  },

  computed: {
    // General size base on mode
    size() {
      return this.visualize ? '157.2px' : '72px'
    },
    // Price text
    priceText() {
      return this.$util.formatMoney(this.value.actualPrice)
    }
  },
}
</script>
