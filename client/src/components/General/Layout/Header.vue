<template>
  <q-header v-bind="$attrs" reveal class="bg-primary text-white">
    <q-toolbar>
      <q-avatar>
        <img src="images/duck.png">
      </q-avatar>
      <TextField v-model="search" class="tw-w-1/4 tw-max-w-lg tw-ml-4"/>
      <OrderBar/>
    </q-toolbar>
  </q-header>

  <RightDrawer/>
</template>

<script>
import RightDrawer from "components/General/Layout/NavigateDrawer.vue";
import TextField from "components/General/Other/TextField.vue";
import OrderBar from "components/General/Layout/OrderBar.vue";

export default {
  name: "Header",

  components: {OrderBar, TextField, RightDrawer},
}
</script>
