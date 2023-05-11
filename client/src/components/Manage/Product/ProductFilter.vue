<template>
  <!-- Category filter -->
  <FilterPanel :title="$t('field.category')" class="tw-mt-3">
    <q-scroll-area class="tw-h-[100px]">
      <q-item v-for="category in categories" tag="label" dense>
        <q-item-section avatar>
          <q-checkbox v-model="filter.categories" :val="category.id" color="primary" dense/>
        </q-item-section>
        <q-item-section>
          <q-item-label>{{ category.name }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>

  <!-- Group filter -->
  <FilterPanel :title="$t('field.group')" class="tw-mt-3">
    <!-- Search for group -->
    <TextField v-model="groupName" label="Search" icon="search"/>

    <q-scroll-area class="tw-h-[160px] tw-mt-3">
      <q-item v-for="group in satisfiedGroup" tag="label" dense>
        <q-item-section avatar>
          <q-radio v-model="filter.group" :val="group.id" color="primary" dense/>
        </q-item-section>
        <q-item-section>
          <q-item-label>{{ group.name }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>

  <!-- Price range filter -->
  <FilterPanel :title="$t('field.price_range')" class="tw-mt-3">
    <q-scroll-area class="tw-h-[130px] tw-mt-3">
      <q-item v-for="range in priceRanges" tag="label" dense>
        <q-item-section avatar>
          <q-radio :model-value="currentPriceRange" :val="`${range.min}-${range.max}`" color="primary" dense
                   @update:model-value="onChangePriceRange"/>
        </q-item-section>
        <q-item-section>
          <q-item-label>
            <span v-if="range.max === ''">
              {{ $t('field.all') }}
            </span>
            <span v-else>
              {{ $t('message.from_to', {from: $util.formatMoney(range.min), to: $util.formatMoney(range.max)}) }}
            </span>
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>

  <!-- Can be accumulated -->
  <FilterPanel :title="$t('field.can_be_accumulated')" class="tw-mt-3">
    <q-scroll-area class="tw-h-[100px] tw-mt-3">
      <q-item v-for="option in booleanOptions" tag="label" dense>
        <q-item-section avatar>
          <q-radio v-model="filter.canBeAccumulated" :val="option" color="primary" dense/>
        </q-item-section>
        <q-item-section>
          <q-item-label>
            {{ option === null ? $t('field.all') : $t(`field.${option}`) }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>

  <!-- Is in business -->
  <FilterPanel :title="$t('field.is_in_business')" class="tw-mt-3">
    <q-scroll-area class="tw-h-[100px] tw-mt-3">
      <q-item v-for="option in booleanOptions" tag="label" dense>
        <q-item-section avatar>
          <q-radio v-model="filter.isInBusiness" :val="option" color="primary" dense/>
        </q-item-section>
        <q-item-section>
          <q-item-label>
            {{ option === null ? $t('field.all') : $t(`field.${option}`) }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";

export default {
  name: "ProductFilter",

  components: {TextField, FilterPanel},

  props: {
    modelValue: Object,
    categories: Array,
    groups: Array
  },

  computed: {
    // Group: All
    getGroupAll() {
      return {
        id: null,
        name: this.$t('field.all')
      }
    },
    // Get satisfied group and always add group: All to front
    satisfiedGroup() {
      // Get satisfiedGroup
      const satisfiedGroup = this.groups?.filter(group => group.name.includes(this.groupName))
      // Add group: All
      satisfiedGroup.unshift(this.getGroupAll)

      return satisfiedGroup
    },
    // Get price range
    priceRanges() {
      return [
        {min: '', max: ''},
        {min: '0', max: '10000'},
        {min: '10000', max: '100000'},
        {min: '100000', max: '1000000'}
      ]
    },
    // Get current price range
    currentPriceRange() {
      return `${this.modelValue.minPrice}-${this.modelValue.maxPrice}`
    },
    // Boolean options for all boolean filter
    booleanOptions() {
      return [null, true, false]
    }
  },

  data: () => ({
    // Support
    groupName: '',

    // Filter
    filter: {
      categories: [],
      group: null,
      minPrice: '',
      maxPrice: '',
      canBeAccumulated: null,
      isInBusiness: null
    },
  }),

  emits: ['request', 'update:modelValue'],

  watch: {
    // Watch to emit filter event
    filter: {
      deep: true,
      handler(value) {
        this.$emit('update:modelValue', value)
        this.$emit('request')
      }
    }
  },

  methods: {
    /**
     * On change price range
     *
     * @param value
     */
    onChangePriceRange(value) {
      // Split by -
      const range = value.split('-');

      // Set value to filter
      this.filter.minPrice = this.$util.nullIfEmpty(range[0])
      this.filter.maxPrice = this.$util.nullIfEmpty(range[1])
    }
  }
}
</script>

<style scoped>

</style>
