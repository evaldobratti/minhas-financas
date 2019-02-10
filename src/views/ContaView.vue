<template>
  <v-container>
    <v-layout>
      <v-flex>
        <v-btn @click="dialogNovoLancamento = true">Novo lançamento</v-btn>
        <v-btn @click="excluirConta" color="error">Excluir conta</v-btn>
        <month-picker v-model="mesAtual" />
      </v-flex>
    </v-layout>
    <v-layout>
      <v-flex>
        <lancamento-list :lancamentos="lancamentos[0]" :saldos="lancamentos[1]" @excluir="excluirLancamento"  />
      </v-flex>
    </v-layout>
    <v-dialog
      v-model="dialogNovoLancamento"
    >
      <v-card>
        <v-card-title class="headline">Novo lançamento na conta {{conta.nome}}</v-card-title>
        <v-card-text>
          <lancamento-form :idConta="conta.id" @salvo="dialogNovoLancamento = false" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import {mapState} from 'vuex'
import LancamentoForm from '../components/LancamentoForm'
import LancamentoList from '../components/LancamentoList'
import moment from 'moment'


export default {
  created() {
    this.validaExistenciaConta()
  },
  data() {
    return {
      dialogNovoLancamento: false,
      mesAtual: moment()
    }
  },
  methods: {
    validaExistenciaConta() {

    },
    excluirConta() {
      if (confirm("Tem certeza disso?"))
        this.$store.dispatch("contaExcluir", this.$route.params.id)
      
    },
    excluirLancamento(lancamento) {
      if (confirm("Tem certeza disso?"))
        this.$store.dispatch("lancamentoExcluir", lancamento)
    }
  },
  computed: {
    conta() {
      return this.$store.state.contas.byId[this.$route.params.id] || {}
    },
    lancamentos() {
      const [de, ate] = this.periodo
      return this.$store.getters.lancamentos([this.conta], de, ate)
    },
    periodo() {
      return [this.mesAtual, this.mesAtual.clone().endOf('month')]
    }

  },
  components: {
    LancamentoForm,
    LancamentoList
  }
}
</script>

<style>

</style>
