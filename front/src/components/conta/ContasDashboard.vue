<template>
    <v-layout row>
      <v-flex xs6>
        <ContaNenhuma v-if="contas.length === 0"></ContaNenhuma>
        <div v-else v-for="conta in contas" :key="conta.id">
          <ContaCard :conta="conta"></ContaCard>
        </div>
      </v-flex>
    </v-layout>
</template>

<script>
import ContaNenhuma from './ContaNenhuma';
import ContaCard from './ContaCard';
import { LOAD_CONTAS } from '../../store/conta';

export default {
  created() {
    this.$store.dispatch(LOAD_CONTAS);
  },
  computed: {
    contas() {
      return this.$store.state.conta.list;
    }
  },
  components: {
    ContaNenhuma,
    ContaCard
  }
}

</script>

<style>

</style>
