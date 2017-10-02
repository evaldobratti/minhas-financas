<template>
  <div>
  <v-card>
    <v-card-text>
      <v-checkbox v-for="conta in contas" :key="conta.id" :label="conta.nome" v-model="contasSelecionadas" :value="conta.id" />
    </v-card-text>
  </v-card>
  <v-card>
    <v-card-text>
      <v-data-table
              :items="lancamentos"
              hide-actions
              class="elevation-5 lancamentos">
              <template slot="headers" scope="props">
                <tr>
                  <th style="width: 10px">
                    Data
                  </th>
                  <th>
                    Local
                  </th>
                  <th style="width: 130px">
                    Categoria
                  </th>
                  <th style="text-align: right; width: 140px">
                    Valor
                  </th>
                  <th style="text-align: right; width: 140px">
                    Saldo
                  </th>
                  <th  style="width: 40px" ></th>
                  <th  style="width: 40px" ></th>
                </tr>
              </template>
              <template slot="items" scope="l">
                <LancamentoLinha :lancamento="l.item" />
              </template>
            </v-data-table>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import LancamentoLinha from '../lancamento/LancamentoLinha';

export default {
  data() {
    return {
      contasSelecionadas: [],
      lancamentos: []
    }
  },
  watch: {
    contasSelecionadas(val) {
      this.lancamentos = this.$store.getters.lancamentosDe(this.contasSelecionadas, 10, 2017);
    }
  },
  computed: {
    contas() {
      return this.$store.state.conta.list;
    }
  },
  components: {
    LancamentoLinha
  }

}
</script>

<style>

</style>
