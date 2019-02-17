<template>
  <form @submit.prevent="salvar">
    <date-picker label="Data" v-model="form.data" />
    <v-text-field ref="descricao" label="Descrição" v-model="form.descricao" />
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
  efetivada: false
}

export default {
  props: ['idConta', 'lancamento'],
  data() {
    return {
      form: Object.assign({}, formOriginal),
      continuarCriando: false
    }
  },
  created() {
    if (this.lancamento) {
      Object.assign(this.form, this.lancamento)
    }
  },
  methods: {
    salvar() {
      const parsed = {
        ...this.form,
        idConta: this.idConta
      }

      this.$store.dispatch('lancamentoSalvar', parsed)

      const data = this.form.data
      Object.assign(this.form, formOriginal)
      this.form.data = data

      if (!this.continuarCriando) {
        this.$emit('salvo')
      } else {
        this.$refs.descricao.focus()
      }
    }
  },
 /* watch: {
    'form.valor'() {
      this.form.valor = Number(this.form.valor)
    }
  }*/
}
</script>

<style>

</style>
