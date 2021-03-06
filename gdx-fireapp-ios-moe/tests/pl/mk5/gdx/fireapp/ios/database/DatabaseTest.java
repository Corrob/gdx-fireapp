/*
 * Copyright 2018 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.mk5.gdx.fireapp.ios.database;

import com.badlogic.gdx.utils.Array;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.moe.natj.general.NatJ;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.Map;

import apple.foundation.NSDictionary;
import apple.foundation.NSMutableDictionary;
import apple.foundation.NSString;
import bindings.google.firebasedatabase.FIRDatabase;
import bindings.google.firebasedatabase.FIRDatabaseQuery;
import bindings.google.firebasedatabase.FIRDatabaseReference;
import bindings.google.firebasedatabase.enums.FIRDataEventType;
import pl.mk5.gdx.fireapp.GdxFIRDatabase;
import pl.mk5.gdx.fireapp.database.ChildEventType;
import pl.mk5.gdx.fireapp.database.Filter;
import pl.mk5.gdx.fireapp.database.FilterType;
import pl.mk5.gdx.fireapp.database.MapConverter;
import pl.mk5.gdx.fireapp.database.OrderByClause;
import pl.mk5.gdx.fireapp.database.OrderByMode;
import pl.mk5.gdx.fireapp.exceptions.DatabaseReferenceNotSetException;
import pl.mk5.gdx.fireapp.functional.Function;
import pl.mk5.gdx.fireapp.ios.GdxIOSAppTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * TODO - find a solutions for NoSuchMethod java.lang.System#arraycopy when using Rule
 */
@PrepareForTest({
        NatJ.class, FIRDatabase.class,
        QueryConnectionStatus.class,
        QuerySetValue.class, QueryReadValue.class, QueryOnDataChange.class,
        QueryRemoveValue.class, QueryUpdateChildren.class, QueryRunTransaction.class,
        NSString.class, DataProcessor.class, NSDictionary.class, NSMutableDictionary.class, NSDictionaryHelper.class,
        GdxFIRDatabase.class
})
public class DatabaseTest extends GdxIOSAppTest {

//    @Rule
//    public final PowerMockRule powerMockRule = new PowerMockRule();

    private FIRDatabase firDatabase;
    private FIRDatabaseReference firDatabaseReference;

    @Before
    public void setup() {
//        super.setup();
        mockStatic(GdxFIRDatabase.class);
        mockStatic(NatJ.class);
        mockStatic(NSString.class);
        mockStatic(FIRDatabase.class);
        mockStatic(DataProcessor.class);
        mockStatic(NSDictionary.class);
        mockStatic(NSMutableDictionary.class);
        mockStatic(NSDictionaryHelper.class);
        firDatabase = mock(FIRDatabase.class);
        firDatabaseReference = mock(FIRDatabaseReference.class);
        when(firDatabase.referenceWithPath(Mockito.anyString())).thenReturn(firDatabaseReference);
        when(FIRDatabase.database()).thenReturn(firDatabase);
        when(DataProcessor.javaDataToIos(Mockito.any())).thenReturn(mock(NSString.class));

    }

    @Test
    public void onConnect() throws Exception {
        // Given
        Database database = new Database();
        mockStatic(QueryConnectionStatus.class);
//        ConnectionStatusQuery query = PowerMockito.spy(new ConnectionStatusQuery(database));
        QueryConnectionStatus query = PowerMockito.mock(QueryConnectionStatus.class);
        when(query.with(Mockito.any(Array.class))).thenReturn(query);
        when(query.with(Mockito.any(OrderByClause.class))).thenReturn(query);
        when(query.withArgs()).thenReturn(query);
        whenNew(QueryConnectionStatus.class).withAnyArguments().thenReturn(query);

        // When
        database.onConnect().subscribe();

        // Then
        // Can't verify new because of PowerMockitoRunner and JaCoCo coverage. Rule does not work here because of MOE
//        PowerMockito.verifyNew(ConnectionStatusQuery.class).withArguments(Mockito.any());
//        Mockito.verify(connectionStatusQuery, VerificationModeFactory.times(1)).execute();
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).observeEventTypeWithBlock(Mockito.anyLong(), (FIRDatabaseQuery.Block_observeEventTypeWithBlock) Mockito.any());
    }

    @Test
    public void inReference() {
        // Given
        Database database = new Database();
        String reference = "/test";

        // When
        database.inReference(reference);

        // Then
        Assert.assertEquals(reference, Whitebox.getInternalState(database, "databasePath"));
        Assert.assertNotNull(Whitebox.getInternalState(database, "dbReference"));
    }

    @Test
    public void setValue() throws Exception {
        // Given
        Database database = new Database();
        mockStatic(QuerySetValue.class);
        QuerySetValue query = PowerMockito.spy(new QuerySetValue(database, "/test"));
        whenNew(QuerySetValue.class).withAnyArguments().thenReturn(query);

        // When
        database.inReference("/test").setValue("test_value").subscribe();

        // Then
//      PowerMockito.verifyNew(SetValueQuery.class).withArguments(Mockito.any());
//      Mockito.verify(query, VerificationModeFactory.times(1)).execute();
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).setValueWithCompletionBlock(Mockito.any(), Mockito.any(FIRDatabaseReferenceCompleteObserver.class));
    }

    @Test
    public void readValue() throws Exception {
        // Given
        Database database = new Database();
        GdxFIRDatabase gdxFIRDatabase = mock(GdxFIRDatabase.class);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mock(MapConverter.class));
        when(GdxFIRDatabase.instance()).thenReturn(mock(GdxFIRDatabase.class));
        mockStatic(QueryReadValue.class);
        QueryReadValue query = PowerMockito.spy(new QueryReadValue(database, "/test"));
        whenNew(QueryReadValue.class).withAnyArguments().thenReturn(query);
        Class dataType = String.class;

        // When
        database.inReference("/test").readValue(dataType).subscribe();

        // Then
//      PowerMockito.verifyNew(SetValueQuery.class).withArguments(Mockito.any());
//      Mockito.verify(query, VerificationModeFactory.times(1)).execute();
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1))
                .observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock(
                        Mockito.anyLong(),
                        Mockito.any(FIRDatabaseQuery.Block_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_1.class),
                        Mockito.any(FIRDatabaseQuery.Block_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_2.class)
                );
    }

    @Test
    public void onDataChange() throws Exception {
        // Given
        Database database = new Database();
        mockStatic(QueryOnDataChange.class);
        QueryOnDataChange query = PowerMockito.spy(new QueryOnDataChange(database, "/test"));
        GdxFIRDatabase gdxFIRDatabase = mock(GdxFIRDatabase.class);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mock(MapConverter.class));
        when(GdxFIRDatabase.inst()).thenReturn(mock(GdxFIRDatabase.class));
        whenNew(QueryOnDataChange.class).withAnyArguments().thenReturn(query);
        Class dataType = String.class;

        // When
        database.inReference("/test").onDataChange(dataType).subscribe();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).observeEventTypeWithBlockWithCancelBlock(
                Mockito.anyLong(),
                Mockito.any(FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_1.class),
                Mockito.any(FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_2.class)
        );
    }

    @Test
    public void onDataChange_detach() throws Exception {
        // Given
        Database database = new Database();
        long handleValue = 10L;
        GdxFIRDatabase gdxFIRDatabase = mock(GdxFIRDatabase.class);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mock(MapConverter.class));
        when(GdxFIRDatabase.inst()).thenReturn(mock(GdxFIRDatabase.class));
        when(firDatabaseReference.observeEventTypeWithBlockWithCancelBlock(
                anyLong(),
                any(FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_1.class),
                any(FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_2.class))).thenReturn(handleValue);
        mockStatic(QueryOnDataChange.class);
        QueryOnDataChange query = PowerMockito.spy(new QueryOnDataChange(database, "/test"));
        whenNew(QueryOnDataChange.class).withAnyArguments().thenReturn(query);
        String databasePath = "/test";


        // When
        database.inReference("/test").onDataChange(String.class).listen().cancel();

        // Then
//      PowerMockito.verifyNew(OnDataChangeQuery.class).withArguments(Mockito.any());
//      Mockito.verify(query, VerificationModeFactory.times(1)).execute();
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).removeObserverWithHandle(Mockito.eq(handleValue));
    }

    @Test
    public void onChildChange() throws Exception {
        // Given
        Database database = new Database();
        mockStatic(QueryOnChildChange.class);
        QueryOnChildChange query = PowerMockito.spy(new QueryOnChildChange(database, "/test"));
        GdxFIRDatabase gdxFIRDatabase = mock(GdxFIRDatabase.class);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mock(MapConverter.class));
        when(GdxFIRDatabase.inst()).thenReturn(mock(GdxFIRDatabase.class));
        whenNew(QueryOnChildChange.class).withAnyArguments().thenReturn(query);
        Class dataType = String.class;

        // When
        database.inReference("/test").onChildChange(dataType, ChildEventType.CHANGED).subscribe();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).observeEventTypeWithBlockWithCancelBlock(
                Mockito.eq(FIRDataEventType.ChildChanged),
                Mockito.any(FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_1.class),
                Mockito.any(FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_2.class)
        );
    }

    @Test
    public void filter() {
        // Given
        Database database = new Database();

        // When
        database.filter(FilterType.EQUAL_TO, 2);
        database.filter(FilterType.LIMIT_FIRST, 3);
        Filter filter1 = (Filter) ((Array) Whitebox.getInternalState(database, "filters")).get(0);
        Filter filter2 = (Filter) ((Array) Whitebox.getInternalState(database, "filters")).get(1);


        // Then
        Assert.assertNotNull(filter1);
        Assert.assertNotNull(filter2);
        Assert.assertEquals(FilterType.EQUAL_TO, filter1.getFilterType());
        Assert.assertEquals(2, filter1.getFilterArguments()[0]);
        Assert.assertEquals(FilterType.LIMIT_FIRST, filter2.getFilterType());
        Assert.assertEquals(3, filter2.getFilterArguments()[0]);
    }

    @Test
    public void orderBy() {
        // Given
        Database database = new Database();

        // When
        database.orderBy(OrderByMode.ORDER_BY_KEY, "test_key");
        OrderByClause orderByClause = Whitebox.getInternalState(database, "orderByClause");

        // Then
        Assert.assertEquals(OrderByMode.ORDER_BY_KEY, orderByClause.getOrderByMode());
        Assert.assertEquals("test_key", orderByClause.getArgument());
    }

    @Test
    public void push() {
        // Given
        Database database = new Database();
        Mockito.when(firDatabaseReference.childByAutoId()).thenReturn(firDatabaseReference);

        // When
        database.inReference("/test").push();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).childByAutoId();
    }

    @Test
    public void removeValue1() throws Exception {
        // Given
        Database database = new Database();
        mockStatic(QueryRemoveValue.class);
        QueryRemoveValue query = PowerMockito.spy(new QueryRemoveValue(database, "/test"));
        whenNew(QueryRemoveValue.class).withAnyArguments().thenReturn(query);
        Class dataType = String.class;

        // When
        database.inReference("/test").removeValue().subscribe();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).removeValueWithCompletionBlock(Mockito.any(FIRDatabaseReference.Block_removeValueWithCompletionBlock.class));
    }

    @Test
    public void updateChildren1() throws Exception {
        // Given
        Database database = new Database();
        mockStatic(QueryUpdateChildren.class);
        QueryUpdateChildren query = PowerMockito.spy(new QueryUpdateChildren(database, "/test"));
        whenNew(QueryUpdateChildren.class).withAnyArguments().thenReturn(query);
        Map mapData = mock(Map.class);

        // When
        database.inReference("/test").updateChildren(mapData).subscribe();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).updateChildValuesWithCompletionBlock(
                Mockito.nullable(NSDictionary.class),
                Mockito.any(FIRDatabaseReference.Block_updateChildValuesWithCompletionBlock.class)
        );
    }

    @Test
    public void transaction() throws Exception {
        // Given
        Database database = new Database();
        Function transactionFunction = mock(Function.class);
        mockStatic(QueryRunTransaction.class);
        QueryRunTransaction query = PowerMockito.spy(new QueryRunTransaction(database, "/test"));
        whenNew(QueryRunTransaction.class).withAnyArguments().thenReturn(query);
        Class dataType = String.class;

        // When
        database.inReference("/test").transaction(dataType, transactionFunction).subscribe();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).runTransactionBlockAndCompletionBlock(
                Mockito.any(FIRDatabaseReference.Block_runTransactionBlockAndCompletionBlock_0.class),
                Mockito.any(FIRDatabaseReference.Block_runTransactionBlockAndCompletionBlock_1.class)
        );
    }

    @Test
    public void setPersistenceEnabled() {
        // Given
        Database database = new Database();

        // When
        database.setPersistenceEnabled(true);

        // Then
        Mockito.verify(firDatabase, VerificationModeFactory.times(1)).setPersistenceEnabled(Mockito.eq(true));
    }

    @Test
    public void keepSynced() {
        // Given
        Database database = new Database();

        // When
        database.inReference("/test").keepSynced(true);

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).keepSynced(Mockito.eq(true));
    }

    @Test
    public void dbReference() {
        // Given
        Database database = new Database();

        // When
        database.inReference("/test");

        // Then
        Assert.assertNotNull(Whitebox.getInternalState(database, "dbReference"));
        Assert.assertEquals("/test", Whitebox.getInternalState(database, "databasePath"));
    }

    @Test(expected = DatabaseReferenceNotSetException.class)
    public void dbReference_missing() {
        // Given
        GdxFIRDatabase gdxFIRDatabase = mock(GdxFIRDatabase.class);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mock(MapConverter.class));
        when(GdxFIRDatabase.instance()).thenReturn(mock(GdxFIRDatabase.class));
        Database database = new Database();

        // When
        database.readValue(String.class);

        // Then
        Assert.fail();
    }

    @Test(expected = DatabaseReferenceNotSetException.class)
    public void dbReference_missing2() {
        // Given
        GdxFIRDatabase gdxFIRDatabase = mock(GdxFIRDatabase.class);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mock(MapConverter.class));
        when(GdxFIRDatabase.instance()).thenReturn(mock(GdxFIRDatabase.class));
        Database database = new Database();

        // When
        database.onDataChange(String.class);

        // Then
        Assert.fail();
    }

    @Test(expected = DatabaseReferenceNotSetException.class)
    public void dbReference_missing3() {
        // Given
        Database database = new Database();

        // When
        database.removeValue();

        // Then
        Assert.fail();
    }

    @Test
    public void terminateOperation() {
        // Given
        Database database = new Database();

        // When
        database.inReference("/test").filter(FilterType.EQUAL_TO, 2).orderBy(OrderByMode.ORDER_BY_VALUE, null);
        database.terminateOperation();

        // Then
        Assert.assertNull(Whitebox.getInternalState(database, "dbReference"));
        Assert.assertNull(Whitebox.getInternalState(database, "databasePath"));
        Assert.assertNull(Whitebox.getInternalState(database, "orderByClause"));
        Assert.assertEquals(0, ((Array) Whitebox.getInternalState(database, "filters")).size);
    }
}