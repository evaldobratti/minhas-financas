defmodule Finances.EntriesFixtures do
  @moduledoc """
  This module defines test helpers for creating
  entities via the `Finances.Entries` context.
  """

  @doc """
  Generate a account.
  """
  def account_fixture(attrs \\ %{}) do
    {:ok, account} =
      attrs
      |> Enum.into(%{
        initial_balance: "120.5",
        name: "some name"
      })
      |> Finances.Entries.create_account()

    account
  end

  @doc """
  Generate a entry.
  """
  def entry_fixture(attrs \\ %{}) do
    {:ok, entry} =
      attrs
      |> Enum.into(%{
        date: ~D[2021-12-04],
        description: "some description",
        is_carried_out: true,
        value: "120.5"
      })
      |> Finances.Entries.create_entry()

    entry
  end

  @doc """
  Generate a recurrency.
  """
  def recurrency_fixture(attrs \\ %{}) do
    {:ok, recurrency} =
      attrs
      |> Enum.into(%{
        date_end: ~D[2021-12-04],
        date_start: ~D[2021-12-04],
        description: "some description",
        frequency: "some frequency",
        is_forever: true,
        is_parcel: true,
        parcel_end: 42,
        parcel_start: 42,
        value: "120.5"
      })
      |> Finances.Entries.create_recurrency()

    recurrency
  end

  @doc """
  Generate a transfer.
  """
  def transfer_fixture(attrs \\ %{}) do
    {:ok, transfer} =
      attrs
      |> Enum.into(%{

      })
      |> Finances.Entries.create_transfer()

    transfer
  end
end
