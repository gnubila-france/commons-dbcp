/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.dbcp2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A dummy {@link Connection}, for testing purposes.
 *
 * @author Rodney Waldhoff
 * @author Dirk Verbeeck
 * @version $Id$
 */
public class TesterConnection implements Connection {
    protected boolean _open = true;
    protected boolean _autoCommit = true;
    protected int _transactionIsolation = 1;
    protected DatabaseMetaData _metaData = new TesterDatabaseMetaData();
    protected String _catalog = null;
    protected Map<String,Class<?>> _typeMap = null;
    protected boolean _readOnly = false;
    protected SQLWarning warnings = null;
    protected String username = null;
    protected Exception failure;

    public TesterConnection(final String username,
            @SuppressWarnings("unused") final String password) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setWarnings(final SQLWarning warning) {
        this.warnings = warning;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkOpen();
        warnings = null;
    }

    @Override
    public void close() throws SQLException {
        checkFailure();
        _open = false;
    }

    @Override
    public void commit() throws SQLException {
        checkOpen();
        if (isReadOnly()) {
            throw new SQLException("Cannot commit a readonly connection");
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkOpen();
        return new TesterStatement(this);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        checkOpen();
        return new TesterStatement(this);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkOpen();
        return _autoCommit;
    }

    @Override
    public String getCatalog() throws SQLException {
        checkOpen();
        return _catalog;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkOpen();
        return _metaData;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        checkOpen();
        return _transactionIsolation;
    }

    @Override
    public Map<String,Class<?>> getTypeMap() throws SQLException {
        checkOpen();
        return _typeMap;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return warnings;
    }

    @Override
    public boolean isClosed() throws SQLException {
        checkFailure();
        return !_open;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkOpen();
        return _readOnly;
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        checkOpen();
        return sql;
    }

    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        checkOpen();
        if ("warning".equals(sql)) {
            setWarnings(new SQLWarning("warning in prepareCall"));
        }
        return new TesterCallableStatement(this);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        checkOpen();
        return new TesterCallableStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        checkOpen();
        if("null".equals(sql)) {
            return null;
        } if("invalid".equals(sql)) {
            throw new SQLException("invalid query");
        } if ("broken".equals(sql)) {
            throw new SQLException("broken connection");
        }
        return new TesterPreparedStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        checkOpen();
        return new TesterPreparedStatement(this, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public void rollback() throws SQLException {
        checkOpen();
        if (isReadOnly()) {
            throw new SQLException("Cannot rollback a readonly connection");
        }
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        checkOpen();
        _autoCommit = autoCommit;
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        checkOpen();
        _catalog = catalog;
    }

    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        checkOpen();
        _readOnly = readOnly;
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        checkOpen();
        _transactionIsolation = level;
    }

    @Override
    public void setTypeMap(final Map<String,Class<?>> map) throws SQLException {
        checkOpen();
        _typeMap = map;
    }

    protected void checkOpen() throws SQLException {
        if(!_open) {
            throw new SQLException("Connection is closed.");
        }
        checkFailure();
    }

    protected void checkFailure() throws SQLException {
        if (failure != null) {
            if(failure instanceof SQLException) {
                throw (SQLException)failure;
            } else {
                throw new SQLException("TesterConnection failure", failure);
            }
        }
    }

    public void setFailure(final Exception failure) {
        this.failure = failure;
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void setHoldability(final int holdability) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public java.sql.Savepoint setSavepoint() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public java.sql.Savepoint setSavepoint(final String name) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void rollback(final java.sql.Savepoint savepoint) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void releaseSavepoint(final java.sql.Savepoint savepoint) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public Statement createStatement(final int resultSetType,
                                     final int resultSetConcurrency,
                                     final int resultSetHoldability)
        throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType,
                                              final int resultSetConcurrency,
                                              final int resultSetHoldability)
        throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType,
                                         final int resultSetConcurrency,
                                         final int resultSetHoldability)
        throws SQLException {
        return prepareCall(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys)
        throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int columnIndexes[])
        throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String columnNames[])
        throws SQLException {
        return prepareStatement(sql);
    }


    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return _open;
    }

    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }

    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public String getClientInfo(final String name) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public String getSchema() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds)
            throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLException("Not implemented.");
    }
}
