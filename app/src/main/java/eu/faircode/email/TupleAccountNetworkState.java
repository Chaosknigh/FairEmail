package eu.faircode.email;

/*
    This file is part of FairEmail.

    FairEmail is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FairEmail is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FairEmail.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2018-2022 by Marcel Bokhorst (M66B)
*/

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

public class TupleAccountNetworkState {
    public boolean enabled;
    @NonNull
    public Bundle command;
    @NonNull
    public ConnectionHelper.NetworkState networkState;
    @NonNull
    public TupleAccountState accountState;

    private JSONObject jconditions;

    public TupleAccountNetworkState(
            boolean enabled,
            boolean scheduled,
            @NonNull Bundle command,
            @NonNull ConnectionHelper.NetworkState networkState,
            @NonNull TupleAccountState accountState) {
        this.enabled = enabled;
        this.command = command;
        this.networkState = networkState;
        this.accountState = accountState;

        this.jconditions = new JSONObject();
        if (!TextUtils.isEmpty(this.accountState.conditions))
            try {
                jconditions = new JSONObject(this.accountState.conditions);
            } catch (Throwable ex) {
                Log.e(ex);
            }

        if (!scheduled && !jconditions.optBoolean("ignore_schedule"))
            this.enabled = false;
    }

    public boolean canConnect() {
        boolean unmetered = jconditions.optBoolean("unmetered");
        return (!unmetered || this.networkState.isUnmetered());
    }

    public boolean canRun() {
        if (!canConnect())
            return false;

        return (this.networkState.isSuitable() && this.accountState.shouldRun(enabled));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TupleAccountNetworkState) {
            TupleAccountNetworkState other = (TupleAccountNetworkState) obj;
            return this.accountState.id.equals(other.accountState.id);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return accountState.id.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return accountState.name;
    }
}

