<template>
  <q-drawer
    v-model="drawer"
    show-if-above

    :mini="miniState"
    @mouseover="miniState = false"
    @mouseout="miniState = true"
    persistent

    side="right"
    overlay
    :width="200"
    :breakpoint="500"
    bordered
  >
    <q-scroll-area class="fit" :horizontal-thumb-style="{ opacity: 0 }">
      <q-list padding>
        <template v-for="link in links">
          <q-item v-if="link.key" :to="link.to" clickable v-ripple>
            <q-item-section avatar>
              <q-icon :name="`fa-solid ${link.icon}`"/>
            </q-item-section>

            <q-item-section>
              {{ $t(`field.${link.key}`) }}
            </q-item-section>
          </q-item>
          <q-separator v-else inset/>
        </template>
      </q-list>
    </q-scroll-area>
  </q-drawer>
</template>

<script>
export default {
  name: "NavigateDrawer",

  data: () => ({
    drawer: true,
    miniState: true,
    links: [
      {
        key: 'store',
        icon: 'fa-store',
        to: '/work'
      },
      {
        key: 'outstream',
        icon: 'fa-hand-holding-dollar',
        to: '/outstream'
      },
      {},
      {
        key: 'product',
        icon: 'fa-box',
        to: '/product'
      },
      {
        key: 'transaction',
        icon: 'fa-arrow-right-arrow-left',
        to: '/transaction'
      },
      {
        key: 'partner',
        icon: 'fa-handshake',
        to: '/partner'
      },
      {},
      {
        key: 'report',
        icon: 'fa-chart-line',
        to: '/report'
      },
    ]
  }),
}
</script>
