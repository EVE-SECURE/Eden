package com.zdonnell.eden.character.detail.wallet;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zdonnell.androideveapi.character.sheet.CharacterSheetResponse;
import com.zdonnell.androideveapi.core.ApiAuth;
import com.zdonnell.androideveapi.core.ApiAuthorization;
import com.zdonnell.androideveapi.eve.reftypes.ApiRefType;
import com.zdonnell.androideveapi.eve.reftypes.RefTypesResponse;
import com.zdonnell.androideveapi.exception.ApiException;
import com.zdonnell.androideveapi.link.ApiExceptionCallback;
import com.zdonnell.androideveapi.link.ILoadingActivity;
import com.zdonnell.androideveapi.link.character.ApiCharacter;
import com.zdonnell.androideveapi.link.eve.ApiEve;
import com.zdonnell.androideveapi.shared.wallet.journal.ApiJournalEntry;
import com.zdonnell.androideveapi.shared.wallet.journal.WalletJournalResponse;
import com.zdonnell.androideveapi.shared.wallet.transactions.ApiWalletTransaction;
import com.zdonnell.androideveapi.shared.wallet.transactions.WalletTransactionsResponse;
import com.zdonnell.eden.R;
import com.zdonnell.eden.character.detail.DetailFragment;

public class WalletFragment extends DetailFragment {

    public static final int TRANSACTION = 0;
    public static final int JOURNAL = 1;

    public static String[] displayTypeNames = new String[2];

    static {
        displayTypeNames[TRANSACTION] = "Wallet Transactions";
        displayTypeNames[JOURNAL] = "Journal Entries";
    }

    private ApiCharacter character;

    private Context context;

    private ListView walletListView;

    private String characterName;

    private NumberFormat formatter = NumberFormat.getInstance();

    private TextView walletBalance;

    private SharedPreferences prefs;

    private SparseArray<ApiRefType> refTypes = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
        prefs = context.getSharedPreferences("eden_wallet_preferences", Context.MODE_PRIVATE);

        parentActivity = (ILoadingActivity) getActivity();

        LinearLayout inflatedView = (LinearLayout) inflater.inflate(R.layout.char_detail_wallet, container, false);

        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        ApiAuth<?> apiAuth = new ApiAuthorization(getArguments().getInt("keyID"), (long) getArguments().getInt("characterID"), getArguments().getString("vCode"));
        character = new ApiCharacter(context, apiAuth);

        characterName = getArguments().getString("characterName");

        walletBalance = (TextView) inflatedView.findViewById(R.id.char_detail_wallet_balance);
        walletListView = (ListView) inflatedView.findViewById(R.id.char_detail_wallet_listview);

        loadData();

        return inflatedView;
    }

    public void updateWalletType(int type) {
        prefs.edit().putInt("wallet_type", type).commit();

        switch (type) {
            case TRANSACTION:
                loadInTransactions();
                break;
            case JOURNAL:
                loadInJournal();
                break;
        }
    }

    public void loadInJournal() {
        character.getWalletJournal(new ApiExceptionCallback<WalletJournalResponse>((ILoadingActivity) getActivity()) {
            @Override
            public void onUpdate(WalletJournalResponse response) {
                Set<ApiJournalEntry> entrySet = response.getAll();
                ApiJournalEntry[] entryArray = new ApiJournalEntry[entrySet.size()];
                entrySet.toArray(entryArray);

                Arrays.sort(entryArray, new WalletSort.Journal.DateTime());
                walletListView.setAdapter(new WalletJournalAdapter(context, entryArray, characterName, refTypes));
            }

            @Override
            public void onError(WalletJournalResponse response, ApiException exception) {

            }
        });

        new ApiEve(context).refTypes(new ApiExceptionCallback<RefTypesResponse>((ILoadingActivity) getActivity()) {
            @Override
            public void onUpdate(RefTypesResponse response) {
                refTypes = new SparseArray<ApiRefType>();
                for (ApiRefType refType : response.getAll())
                    refTypes.put(refType.getRefTypeID(), refType);

                if (walletListView.getAdapter() != null && walletListView.getAdapter() instanceof WalletJournalAdapter)
                    ((WalletJournalAdapter) walletListView.getAdapter()).provideRefTypes(refTypes);
            }

            @Override
            public void onError(RefTypesResponse response, ApiException exception) {

            }

        });
    }

    public void loadInTransactions() {
        character.getWalletTransactions(new ApiExceptionCallback<WalletTransactionsResponse>((ILoadingActivity) getActivity()) {
            @Override
            public void onUpdate(WalletTransactionsResponse response) {
                Set<ApiWalletTransaction> transactionSet = response.getAll();
                ApiWalletTransaction[] transactionsArray = new ApiWalletTransaction[transactionSet.size()];
                transactionSet.toArray(transactionsArray);

                Arrays.sort(transactionsArray, new WalletSort.Transactions.DateTime());
                walletListView.setAdapter(new WalletTransactionAdapter(context, transactionsArray));
            }

            @Override
            public void onError(WalletTransactionsResponse response, ApiException exception) {

            }
        });
    }

    @Override
    public void loadData() {
        switch (prefs.getInt("wallet_type", JOURNAL)) {
            case JOURNAL:
                loadInJournal();
                break;
            case TRANSACTION:
                loadInTransactions();
                break;
        }

        character.getCharacterSheet(new ApiExceptionCallback<CharacterSheetResponse>((ILoadingActivity) getActivity()) {
            @Override
            public void onUpdate(CharacterSheetResponse response) {
                walletBalance.setText(formatter.format(response.getBalance()) + " ISK");
            }

            @Override
            public void onError(CharacterSheetResponse response, ApiException exception) {

            }
        });
    }
}
