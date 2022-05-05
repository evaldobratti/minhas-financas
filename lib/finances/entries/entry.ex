defmodule Finances.Entries.Entry do
  use Ecto.Schema
  import Ecto.Changeset

  schema "entries" do
    field :date, :date
    field :description, :string
    field :is_carried_out, :boolean, default: false
    field :value, :decimal
    field :account_id, :id

    timestamps()
  end

  @doc false
  def changeset(entry, attrs) do
    entry
    |> cast(attrs, [:date, :description, :is_carried_out, :value, :account_id])
    |> validate_required([:date, :description, :is_carried_out, :value, :account_id])
  end
end
