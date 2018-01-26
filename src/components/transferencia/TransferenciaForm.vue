<template>
  <div>
    <form @submit.prevent="submit">
      <v-select
        label="Conta"
        v-bind:items="contas"
        v-model="idContaDestino"
        item-text="nome"
        item-value="id"
        max-height="auto">
      </v-select>
      <v-btn type="submit">Salvar</v-btn>
    </form>
  </div>
</template>

<script>
import TRANSFERENCIAS from '../../store/transferencias';

export default {
  props: ['lancamento'],
  data() {
    return {
      idContaDestino: null
    }
  },
  created() {
    this.idContaDestino = this.contas[0].id;
  },
  computed: {
    contas() {
      return this.$store.getters.getContas.filter(c => c.id != this.lancamento.idConta);
    }
  },
  methods: {
    submit() {
      this.$store.dispatch(TRANSFERENCIAS.d.SUBMIT, { lancamento: this.lancamento, idContaDestino: this.idContaDestino });
    }
  }
}
</script>

<style>

</style>
