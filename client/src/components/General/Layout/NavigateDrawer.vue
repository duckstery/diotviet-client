<template>
  <q-drawer
    v-bind="$attrs"

    overlay
    bordered
    side="right"
    show-if-above

    :width="200"
    :mini="miniState"
    :persistent="$q.screen.gt.sm"
    :breakpoint="$q.screen.sizes.md - 1"

    @mouseover="miniState = false"
    @mouseout="miniState = true"
  >
    <q-img v-if="$q.screen.lt.md" :src="imageSrc" style="height: 150px; z-index: 1">
      <div class="absolute tw-w-full bg-transparent">
        <q-avatar size="56px" class="q-mb-sm">
          <IconMage src="/images/man.png" color="white" size="56px"/>
        </q-avatar>
        <div class=" text-weight-bold">{{ name }}</div>
        <div>{{ $t(`field.role_${privilege}`) }}</div>

        <Button flat icon="fa-solid fa-arrow-left-long" color="white" class="tw-mr-3 tw-mt-3 absolute-top-right"
                :tooltip="$t('field.close')" @click="$emit('close')"/>
        <Button flat icon="fa-solid fa-right-from-bracket" color="white" class="tw-mr-3 absolute-bottom-right"
                :tooltip="$t('field.logout')" @click="$emit('logout')"/>
      </div>
    </q-img>

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
import IconMage from "components/General/Other/IconMage.vue";
import Button from "components/General/Other/Button.vue";

import {mapState} from "pinia";
import {useAuthStore} from "stores/auth";

export default {
  name: "NavigateDrawer",

  components: {Button, IconMage},

  emits: ['close', 'logout'],

  data: () => ({
    miniState: true,
  }),

  computed: {
    ...mapState(useAuthStore, ['name', 'privilege']),
    imageSrc() {
      return `https://picsum.photos/500/300?t=${Math.random()}`
    },
    // Default
    links() {
      const links = this.$q.screen.lt.md ? [] : [
        {
          key: 'store',
          icon: 'fa-store',
          to: '/work'
        },
        {},
      ]

      // Push common links
      links.push(
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
        }
      )

      return links
    }
  }
}
</script>
