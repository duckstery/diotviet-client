<template>
  <q-tab-panels v-model="tab" animated class="bg-warning tw-h-full tw-w-full">
    <q-tab-panel name="mails">
      <div class="text-h6">Mails</div>
      Lorem ipsum dolor sit amet consectetur adipisicing elit.
    </q-tab-panel>

    <q-tab-panel name="alarms">
      <div class="text-h6">Alarms</div>
      Lorem ipsum dolor sit amet consectetur adipisicing elit.
    </q-tab-panel>

    <q-tab-panel name="movies">
      <div class="text-h6">Movies</div>
      Lorem ipsum dolor sit amet consectetur adipisicing elit.
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import OrderItem from "components/Work/OrderItem.vue";

export default {
  name: 'OrderPanel',

  components: {OrderItem},

  setup () {
    return {}
  }
}
</script>
