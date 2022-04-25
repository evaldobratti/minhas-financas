defmodule Test do
   defstruct [:foo, :bar, :baz]
   
   defimpl Jason.Encoder do
      @impl Jason.Encoder 
      def encode(value, opts) do
         Jason.Encode.map(%{foo: Map.get(value, :foo), banana: Map.get(value, :bar, 0) + Map.get(value, :baz, 0)}, opts)
      end
   end
end
