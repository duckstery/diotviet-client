<template>
  <div>
    <div class="row">
      <div v-for="op in operations" class="col-6">
        <q-card v-ripple flat bordered :key="op.key" @click="onExecuteOperation(op.key)">
          <q-img :src="`images/${op.key}.png`" fit="contain"/>

          <q-card-section>
            <q-avatar class="tw-absolute tw-top-0 tw-right-3 -tw-translate-y-1/2" :icon="op.icon" :color="op.color"/>

            <div class="row no-wrap items-center">
              <div class="col text-h6 ellipsis">
                {{ $t(`field.${op.key}`) }}
              </div>
            </div>
          </q-card-section>

          <q-card-section class="q-pt-none">
            <div>
              {{ $t(`message.${op.key}_manage_hint`) }}
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </div>
</template>

<script>
import {useRouter} from "vue-router";

export default {
  name: 'ManagePanel',

  setup() {
    // Get router
    const router = useRouter()

    // On click operation
    const onExecuteOperation = (key) => {
      router.push({path: `/operation/${key}`})
    }

    return {
      operations: [
        {icon: 'receipt', key: 'process_order', color: 'secondary'},
        {icon: 'fa-solid fa-hand-holding-dollar', key: 'create_transaction', color: 'info'},
        {icon: 'fa-solid fa-user-shield', key: 'inspect_identity', color: 'positive'},
      ],
      onExecuteOperation: onExecuteOperation
    }
  }
}
</script>
