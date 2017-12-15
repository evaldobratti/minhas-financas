<template>
<div>
  <v-select
    ref="autoComplete"
    v-model="internal"
    :items="locais"
    autocomplete
    @input="$emit('input', $event)"
    :label="label"
    :search-input.sync="maybeNewValue">
      <template slot="item" slot-scope="data">
        {{ data.item }}
      </template>
  </v-select>
</div>
</template>

<script>
export default {
  props: ['value', 'label'],
  data() {
    return {
      maybeNewValue: '',
      internal: null,
      locais: []
    }
  },
  created() {
    this.internal = this.value;
  },
  watch: {
    value(val) {
      if (val != null) {
        this.maybeNewLocal(val.nome);
      }
      this.internal = val;
    },
    maybeNewValue(val) {
      this.maybeNewLocal(val);
    },
    locaisExistentes() {
      if (this.locais.length == 0)
        this.locais = this.locaisExistentes
    }
  },
  methods: {
    maybeNewLocal(val) {
      if (val == null)
        return;
      
      if (val != null && val.length > 0) {
        let possible = this.locaisExistentes.find(c => c.toLowerCase().startsWith(val.toLowerCase()));
        if (possible == null) {
            this.locais = [...this.locaisExistentes, val];
        }
      } else {
        this.locais = this.locaisExistentes;
      }
    },
    refresh() {
      if (this.value.id == null) {
        this.$store.state.locais.list = this.$store.state.locais.list.filter(c => c.id != null)
        
        this.$store.state.locais.list = [...this.$store.state.locais.list, this.value ];
        
      }
      this.internal = this.value;
    },
    focus() {
      this.$refs.autoComplete.focus();  
      this.$refs.autoComplete.$refs.input.focus();
    },

  },
  computed: {
    locaisExistentes() {
      return this.$store.state.locais.list;
    }
  }
}
</script>

<style>

</style>
