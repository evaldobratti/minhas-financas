<template>
  <form @submit.prevent="submit" style="min-width: 600px">
    <v-layout row>
      <v-flex xs2>
        <v-text-field 
          label="A cada"
          v-model="aCada"></v-text-field>  
      </v-flex>
      <v-flex xs5>
        <v-select
          label=""
          :items="frequencias"
          item-text="text"
          item-value="value"
          v-model="frequencia"></v-select>  
      </v-flex>
      <v-flex xs2>
        <v-text-field 
          label="Valor"
          v-model="valor"></v-text-field>  
      </v-flex>
      <v-flex xs2>
        <v-text-field 
          label="A partir de"
          v-model="dia"></v-text-field>  
      </v-flex>
      
    </v-layout>
    <v-layout row>
      <v-flex xs5>
        <v-btn type="submit">Salvar</v-btn>
      </v-flex>
    </v-layout>
  </form>
</template>

<script>
export default {
  props: ['lancamento'],
  data() {
    return {
      frequencias: [{
        value: 'Diaria',
        text: 'Dia'
      }, {
        value: 'Semanal',
        text: 'Semana'
      }, {
        value: 'Mensal',
        text: 'Mês'
      }, {
        value: 'Anual',
        text: 'Ano'
      }],
      frequencia: 'Mensal',
      aCada: 1,
      valor: 0,
      dia: 20
    }
  }, 
  watch: {
    lancamento(l) {
      this.valor = l.valor;
      this.dia = l.data;
    }
  }
}
</script>

<style>
  
</style>
