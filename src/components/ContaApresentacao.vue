<template>
  <v-container v-if="conta != null">
    <v-layout>
      <v-flex>
        <v-btn @click="dialogNovoLancamento = true">Novo lançamento</v-btn>
        <v-btn @click="excluirConta" color="error">Excluir conta</v-btn>
      </v-flex>
    </v-layout>
    <v-layout>
      <v-flex><v-btn @click="mesAnterior">Anterior</v-btn></v-flex>
      <v-flex><month-picker v-model="mesAtual" /></v-flex>
      <v-flex><v-btn @click="mesProximo">Próximo</v-btn></v-flex>
    </v-layout>
    <v-layout>
      <v-flex>
        <lancamento-list 
          :lancamentos="lancamentos[0]" 
          :saldos="lancamentos[1]" 
          @excluir="excluirLancamento"
          @alternaEfetiva="alternaEfetiva"  
          @subir="subirLancamento"
          @descer="descerLancamento"
        />
      </v-flex>
    </v-layout>
    <v-dialog
      v-model="dialogNovoLancamento"
      width="400"
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
  props: ['conta'],
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
      if (confirm("Tem certeza disso?")) {
        this.$store.dispatch("contaExcluir", this.conta.id)
        this.$router.push({
          name: 'home'
        })
      }
      
    },
    excluirLancamento(lancamento) {
      if (confirm("Tem certeza disso?"))
        this.$store.dispatch("lancamentoExcluir", lancamento)
    },
    alternaEfetiva(lancamento) {
      this.$store.dispatch("lancamentoSalvar", lancamento)
    },
    subirLancamento(lancamento) {
      this.$store.dispatch("lancamentoSubir", lancamento)
    },
    descerLancamento(lancamento) {
      this.$store.dispatch("lancamentoDescer", lancamento)
    },
    mesAnterior() {
      this.mesAtual = moment(this.mesAtual).add(-1, 'month')
    },
    mesProximo() {
      this.mesAtual = moment(this.mesAtual).add(1, 'month')
    }
  },
  computed: {
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
