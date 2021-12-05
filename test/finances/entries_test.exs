defmodule Finances.EntriesTest do
  use Finances.DataCase

  alias Finances.Entries

  describe "accounts" do
    alias Finances.Entries.Account

    import Finances.EntriesFixtures

    @invalid_attrs %{initial_balance: nil, name: nil}

    test "list_accounts/0 returns all accounts" do
      account = account_fixture()
      assert Entries.list_accounts() == [account]
    end

    test "get_account!/1 returns the account with given id" do
      account = account_fixture()
      assert Entries.get_account!(account.id) == account
    end

    test "create_account/1 with valid data creates a account" do
      valid_attrs = %{initial_balance: "120.5", name: "some name"}

      assert {:ok, %Account{} = account} = Entries.create_account(valid_attrs)
      assert account.initial_balance == Decimal.new("120.5")
      assert account.name == "some name"
    end

    test "create_account/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Entries.create_account(@invalid_attrs)
    end

    test "update_account/2 with valid data updates the account" do
      account = account_fixture()
      update_attrs = %{initial_balance: "456.7", name: "some updated name"}

      assert {:ok, %Account{} = account} = Entries.update_account(account, update_attrs)
      assert account.initial_balance == Decimal.new("456.7")
      assert account.name == "some updated name"
    end

    test "update_account/2 with invalid data returns error changeset" do
      account = account_fixture()
      assert {:error, %Ecto.Changeset{}} = Entries.update_account(account, @invalid_attrs)
      assert account == Entries.get_account!(account.id)
    end

    test "delete_account/1 deletes the account" do
      account = account_fixture()
      assert {:ok, %Account{}} = Entries.delete_account(account)
      assert_raise Ecto.NoResultsError, fn -> Entries.get_account!(account.id) end
    end

    test "change_account/1 returns a account changeset" do
      account = account_fixture()
      assert %Ecto.Changeset{} = Entries.change_account(account)
    end
  end

  describe "entries" do
    alias Finances.Entries.Entry

    import Finances.EntriesFixtures

    @invalid_attrs %{date: nil, description: nil, is_carried_out: nil, value: nil}

    test "list_entries/0 returns all entries" do
      entry = entry_fixture()
      assert Entries.list_entries() == [entry]
    end

    test "get_entry!/1 returns the entry with given id" do
      entry = entry_fixture()
      assert Entries.get_entry!(entry.id) == entry
    end

    test "create_entry/1 with valid data creates a entry" do
      valid_attrs = %{date: ~D[2021-12-04], description: "some description", is_carried_out: true, value: "120.5"}

      assert {:ok, %Entry{} = entry} = Entries.create_entry(valid_attrs)
      assert entry.date == ~D[2021-12-04]
      assert entry.description == "some description"
      assert entry.is_carried_out == true
      assert entry.value == Decimal.new("120.5")
    end

    test "create_entry/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Entries.create_entry(@invalid_attrs)
    end

    test "update_entry/2 with valid data updates the entry" do
      entry = entry_fixture()
      update_attrs = %{date: ~D[2021-12-05], description: "some updated description", is_carried_out: false, value: "456.7"}

      assert {:ok, %Entry{} = entry} = Entries.update_entry(entry, update_attrs)
      assert entry.date == ~D[2021-12-05]
      assert entry.description == "some updated description"
      assert entry.is_carried_out == false
      assert entry.value == Decimal.new("456.7")
    end

    test "update_entry/2 with invalid data returns error changeset" do
      entry = entry_fixture()
      assert {:error, %Ecto.Changeset{}} = Entries.update_entry(entry, @invalid_attrs)
      assert entry == Entries.get_entry!(entry.id)
    end

    test "delete_entry/1 deletes the entry" do
      entry = entry_fixture()
      assert {:ok, %Entry{}} = Entries.delete_entry(entry)
      assert_raise Ecto.NoResultsError, fn -> Entries.get_entry!(entry.id) end
    end

    test "change_entry/1 returns a entry changeset" do
      entry = entry_fixture()
      assert %Ecto.Changeset{} = Entries.change_entry(entry)
    end
  end

  describe "recurrencies" do
    alias Finances.Entries.Recurrency

    import Finances.EntriesFixtures

    @invalid_attrs %{date_end: nil, date_start: nil, description: nil, frequency: nil, is_forever: nil, is_parcel: nil, parcel_end: nil, parcel_start: nil, value: nil}

    test "list_recurrencies/0 returns all recurrencies" do
      recurrency = recurrency_fixture()
      assert Entries.list_recurrencies() == [recurrency]
    end

    test "get_recurrency!/1 returns the recurrency with given id" do
      recurrency = recurrency_fixture()
      assert Entries.get_recurrency!(recurrency.id) == recurrency
    end

    test "create_recurrency/1 with valid data creates a recurrency" do
      valid_attrs = %{date_end: ~D[2021-12-04], date_start: ~D[2021-12-04], description: "some description", frequency: "some frequency", is_forever: true, is_parcel: true, parcel_end: 42, parcel_start: 42, value: "120.5"}

      assert {:ok, %Recurrency{} = recurrency} = Entries.create_recurrency(valid_attrs)
      assert recurrency.date_end == ~D[2021-12-04]
      assert recurrency.date_start == ~D[2021-12-04]
      assert recurrency.description == "some description"
      assert recurrency.frequency == "some frequency"
      assert recurrency.is_forever == true
      assert recurrency.is_parcel == true
      assert recurrency.parcel_end == 42
      assert recurrency.parcel_start == 42
      assert recurrency.value == Decimal.new("120.5")
    end

    test "create_recurrency/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Entries.create_recurrency(@invalid_attrs)
    end

    test "update_recurrency/2 with valid data updates the recurrency" do
      recurrency = recurrency_fixture()
      update_attrs = %{date_end: ~D[2021-12-05], date_start: ~D[2021-12-05], description: "some updated description", frequency: "some updated frequency", is_forever: false, is_parcel: false, parcel_end: 43, parcel_start: 43, value: "456.7"}

      assert {:ok, %Recurrency{} = recurrency} = Entries.update_recurrency(recurrency, update_attrs)
      assert recurrency.date_end == ~D[2021-12-05]
      assert recurrency.date_start == ~D[2021-12-05]
      assert recurrency.description == "some updated description"
      assert recurrency.frequency == "some updated frequency"
      assert recurrency.is_forever == false
      assert recurrency.is_parcel == false
      assert recurrency.parcel_end == 43
      assert recurrency.parcel_start == 43
      assert recurrency.value == Decimal.new("456.7")
    end

    test "update_recurrency/2 with invalid data returns error changeset" do
      recurrency = recurrency_fixture()
      assert {:error, %Ecto.Changeset{}} = Entries.update_recurrency(recurrency, @invalid_attrs)
      assert recurrency == Entries.get_recurrency!(recurrency.id)
    end

    test "delete_recurrency/1 deletes the recurrency" do
      recurrency = recurrency_fixture()
      assert {:ok, %Recurrency{}} = Entries.delete_recurrency(recurrency)
      assert_raise Ecto.NoResultsError, fn -> Entries.get_recurrency!(recurrency.id) end
    end

    test "change_recurrency/1 returns a recurrency changeset" do
      recurrency = recurrency_fixture()
      assert %Ecto.Changeset{} = Entries.change_recurrency(recurrency)
    end
  end

  describe "transfers" do
    alias Finances.Entries.Transfer

    import Finances.EntriesFixtures

    @invalid_attrs %{}

    test "list_transfers/0 returns all transfers" do
      transfer = transfer_fixture()
      assert Entries.list_transfers() == [transfer]
    end

    test "get_transfer!/1 returns the transfer with given id" do
      transfer = transfer_fixture()
      assert Entries.get_transfer!(transfer.id) == transfer
    end

    test "create_transfer/1 with valid data creates a transfer" do
      valid_attrs = %{}

      assert {:ok, %Transfer{} = transfer} = Entries.create_transfer(valid_attrs)
    end

    test "create_transfer/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Entries.create_transfer(@invalid_attrs)
    end

    test "update_transfer/2 with valid data updates the transfer" do
      transfer = transfer_fixture()
      update_attrs = %{}

      assert {:ok, %Transfer{} = transfer} = Entries.update_transfer(transfer, update_attrs)
    end

    test "update_transfer/2 with invalid data returns error changeset" do
      transfer = transfer_fixture()
      assert {:error, %Ecto.Changeset{}} = Entries.update_transfer(transfer, @invalid_attrs)
      assert transfer == Entries.get_transfer!(transfer.id)
    end

    test "delete_transfer/1 deletes the transfer" do
      transfer = transfer_fixture()
      assert {:ok, %Transfer{}} = Entries.delete_transfer(transfer)
      assert_raise Ecto.NoResultsError, fn -> Entries.get_transfer!(transfer.id) end
    end

    test "change_transfer/1 returns a transfer changeset" do
      transfer = transfer_fixture()
      assert %Ecto.Changeset{} = Entries.change_transfer(transfer)
    end
  end
end
