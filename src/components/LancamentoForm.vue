<template>
  <form @submit.prevent="salvar">
    <v-alert
      v-if="form.id != null && isTransferencia"
      :value="true"
      type="info"
    >
      Lançamento referente a transferência, as alterações serão refletidas no lançamento de contra partida.
    </v-alert>
    <date-picker label="Data" v-model="form.data" />
    <v-switch :disabled="form.id != null"  v-model="isTransferencia" label="Transferência" />
    <v-select :disabled="form.id != null" v-if="isTransferencia" :items="contas" label="Para conta" item-text="nome" item-value="id" v-model="form.idContaDestino" />
    <v-text-field v-if="!isTransferencia" ref="descricao" label="Descrição" v-model="form.descricao" />
    <v-text-field label="Valor" v-model.number="form.valor" />

    <v-switch v-model="form.efetivada" label="Efetivada" />
    <v-switch v-if="form.id == null" v-model="continuarCriando" label="Continuar criando" />
    <v-btn type="submit">Salvar</v-btn>
  </form>
</template>

<script>
import moment from 'moment'

const formOriginal = {
  data: moment(),
  descricao: '',
  valor: null,
  efetivada: false,
  idContaDestino: null
}

export default {
  props: ['idConta', 'lancamento'],
  data() {
    return {
      form: Object.assign({}, formOriginal),
      continuarCriando: false,
      isTransferencia: false
    }
  },
  created() {
    if (this.lancamento) {
      this.isTransferencia = this.lancamento.idContaDestino != null
      Object.assign(this.form, this.lancamento)
    }
  },
  methods: {
    salvar() {
      const parsed = {
        ...this.form,
        idConta: this.idConta
      }

      if (this.isTransferencia && parsed.idContaDestino != null) {
        this.$store.dispatch('transferenciaSalvar', parsed)
      } else {
        this.$store.dispatch('lancamentoSalvar', parsed)
      }

      const data = this.form.data
      Object.assign(this.form, formOriginal)
      this.form.data = data
      this.isTransferencia  = false
      if (!this.continuarCriando) {
        this.$emit('salvo')
      } else {
        this.$refs.descricao.focus()
      }
    }
  },
  computed: {
    contas() {
      return this.$store.getters.contas.filter(c => c.id !== this.idConta);
    }
  }
}
</script>

<style>

</style>
