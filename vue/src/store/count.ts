import { ref, computed } from "vue";
import { defineStore } from "pinia";
import type { StoreDefinition } from "pinia";

export const useModelStore: StoreDefinition = defineStore("model", () => {
  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)
  function acount () {}

  return {
    count,
    doubleCount,
    acount
  }
})