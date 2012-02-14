/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/tom/workspace/gbw-themes/ipc-plugin-library/src/net/hubalek/android/gaugebattwidget/themes/library/IStatusBarInfoRemoteService.aidl
 */
package net.hubalek.android.gaugebattwidget.themes.library;
public interface IStatusBarInfoRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService
{
private static final java.lang.String DESCRIPTOR = "net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService interface,
 * generating a proxy if needed.
 */
public static net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService))) {
return ((net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService)iin);
}
return new net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_updateStatusBarInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
java.lang.String _arg3;
_arg3 = data.readString();
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _arg5;
_arg5 = data.readString();
this.updateStatusBarInfo(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_hideStatusBarInfo:
{
data.enforceInterface(DESCRIPTOR);
this.hideStatusBarInfo();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.hubalek.android.gaugebattwidget.themes.library.IStatusBarInfoRemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void updateStatusBarInfo(java.lang.String title, java.lang.String text, int level, java.lang.String callbackPackageName, java.lang.String callbackActivityName, java.lang.String callbackServiceName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(title);
_data.writeString(text);
_data.writeInt(level);
_data.writeString(callbackPackageName);
_data.writeString(callbackActivityName);
_data.writeString(callbackServiceName);
mRemote.transact(Stub.TRANSACTION_updateStatusBarInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void hideStatusBarInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hideStatusBarInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_updateStatusBarInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_hideStatusBarInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void updateStatusBarInfo(java.lang.String title, java.lang.String text, int level, java.lang.String callbackPackageName, java.lang.String callbackActivityName, java.lang.String callbackServiceName) throws android.os.RemoteException;
public void hideStatusBarInfo() throws android.os.RemoteException;
}
