<template>
    <v-layout row>
      <v-flex xs6>
        <ContaNenhuma v-if="contas.length === 0"></ContaNenhuma>
        <div v-else v-for="conta in contas" :key="conta.id">
          <ContaCard :conta="conta"></ContaCard>
        </div>
        <v-dialog v-model="novaConta" lazy absolute>
          <v-btn primary dark slot="activator">nova conta</v-btn>
          <ContaForm @cadastrado="novaConta = false"></ContaForm>
        </v-dialog>
      </v-flex>
    </v-layout>
</template>

<script>
import ContaNenhuma from './ContaNenhuma';
import ContaCard from './ContaCard';
import ContaForm from './ContaForm';
import { LOAD_CONTAS } from '../../store/conta';

export default {
  created() {
    this.$store.dispatch(LOAD_CONTAS);
  },
  data() {
    return {
      novaConta: false
    }
  },
  computed: {
    contas() {
      return this.$store.state.conta.list;
    }
  },
  components: {
    ContaNenhuma,
    ContaCard,
    ContaForm
  }
}

</script>

<style>

</style>
