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

    <v-tabs
      v-model="abaAtiva"
      color="cyan"
      dark
      slider-color="yellow"
      :disabled="form.id != null"
    >
      <v-tab
        :key="0"
        ripple
      >
        avulso
      </v-tab>
      <v-tab
        :key="1"
        ripple
      >
        transferencia
      </v-tab>
      <v-tab-item :key="0">
        <v-text-field ref="descricao" label="Descrição" v-model="form.descricao" />
      </v-tab-item>
      <v-tab-item :key="1">
        <v-select :items="contas" label="Para conta" item-text="nome" item-value="id" v-model="form.idContaDestino" />
      </v-tab-item>
    </v-tabs>

    <v-text-field label="Valor" v-model.number="form.valor" />

    <v-layout row flex>
      <v-flex>
        <v-switch v-model="recorrencia.isRecorrente" label="Repetir" />
      </v-flex>
      <v-flex>
        <v-switch v-model="form.efetivada" label="Efetivada" />
      </v-flex>
      <v-flex>
        <v-switch v-if="form.id == null" v-model="continuarCriando" label="Continuar criando" />
      </v-flex>
    </v-layout>
    <div v-if="recorrencia.isRecorrente">
        <v-switch v-model="recorrencia.isIndefinidamente" label="Repetir indefinidamente" />
        <v-text-field v-if="!recorrencia.isIndefinidamente" label="Iniciar na parcela" v-model.number="recorrencia.parcelaInicio" />
        <v-text-field v-if="!recorrencia.isIndefinidamente" label="até" v-model.number="recorrencia.parcelaFim" />
      </div>
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
  idContaDestino: null,
}

export default {
  props: ['idConta', 'lancamento'],
  data() {
    return {
      form: Object.assign({}, formOriginal),
      continuarCriando: false,
      isTransferencia: false,
      isRecorrencia: false,
      abaAtiva: 0,
      recorrencia: {
        isRecorrente: false,
        isIndefinidamente: false,
        parcelaInicio: 1,
        parcelaFim: null
      }
    }
  },
  created() {
    if (this.lancamento) {
      this.isTransferencia = this.lancamento.idContaDestino != null
      Object.assign(this.form, this.lancamento)
    }
  },
  watch: {
    abaAtiva() {
      this.isTransferencia = this.abaAtiva === 1
      this.isRecorrencia = this.abaAtiva === 2
      
      if (!this.isTransferencia) {
        this.form.idContaDestino = null
      }
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
      } else if (this.recorrencia.isRecorrente && parsed.id == null) {
        this.$store.dispatch('recorrenciaSalvar', {
          recorrenciaBase: this.recorrencia,
          lancamentoBase: parsed
        })
      } else {
        this.$store.dispatch('lancamentoSalvar', parsed)
      }
      
      if (!this.continuarCriando) {
        this.$emit('salvo')
      } else {
        this.$refs.descricao.focus()
      }

      if (!this.form.id) {
        const data = this.form.data
        this.isTransferencia  = false
        Object.assign(this.form, formOriginal)
        this.form.data = data
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
