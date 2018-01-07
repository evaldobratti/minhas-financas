<template>
    <v-layout row>
      <v-flex xs6>
        <v-dialog v-model="novaConta" lazy absolute>
          <v-btn color="primary" dark slot="activator">nova conta</v-btn>
          <ContaForm @cadastrado="novaConta = false"></ContaForm>
        </v-dialog>
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
import ContaForm from './ContaForm';

export default {
  data() {
    return {
      novaConta: false
    }
  },
  computed: {
    contas() {
      return this.$store.getters.getContas;
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
