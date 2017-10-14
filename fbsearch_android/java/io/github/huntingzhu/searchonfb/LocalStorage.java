package io.github.huntingzhu.searchonfb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LocalStorage
{
    public static final String FILE_USER = "file_user";
    public static final String FILE_PAGE = "file_page";
    public static final String FILE_EVENT = "file_event";
    public static final String FILE_PLACE = "file_place";
    public static final String FILE_GROUP = "file_group";



    public static void put(Context context, String key, String value, String type)
    {
        String fileName = null;
        switch (type) {
            case "user":
                fileName = FILE_USER;
                break;
            case "page":
                fileName = FILE_PAGE;
                break;
            case "event":
                fileName = FILE_EVENT;
                break;
            case "place":
                fileName = FILE_PLACE;
                break;
            case "group":
                fileName = FILE_GROUP;
                break;
            default:
                break;
        }

        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static String get(Context context, String key, String type)
    {
        String fileName = null;
        switch (type) {
            case "user":
                fileName = FILE_USER;
                break;
            case "page":
                fileName = FILE_PAGE;
                break;
            case "event":
                fileName = FILE_EVENT;
                break;
            case "place":
                fileName = FILE_PLACE;
                break;
            case "group":
                fileName = FILE_GROUP;
                break;
            default:
                break;
        }
        return context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE).getString(key, null);
    }

    public static void remove(Context context, String key, String type)
    {
        String fileName = null;
        switch (type) {
            case "user":
                fileName = FILE_USER;
                break;
            case "page":
                fileName = FILE_PAGE;
                break;
            case "event":
                fileName = FILE_EVENT;
                break;
            case "place":
                fileName = FILE_PLACE;
                break;
            case "group":
                fileName = FILE_GROUP;
                break;
            default:
                break;
        }
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    public static boolean contains(Context context, String key, String type)
    {
        String fileName = null;
        switch (type) {
            case "user":
                fileName = FILE_USER;
                break;
            case "page":
                fileName = FILE_PAGE;
                break;
            case "event":
                fileName = FILE_EVENT;
                break;
            case "place":
                fileName = FILE_PLACE;
                break;
            case "group":
                fileName = FILE_GROUP;
                break;
            default:
                break;
        }
        return context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE).contains(key);
    }

    public static Map<String, ?> getAll(Context context, String type)
    {
        String fileName = null;
        switch (type) {
            case "user":
                fileName = FILE_USER;
                break;
            case "page":
                fileName = FILE_PAGE;
                break;
            case "event":
                fileName = FILE_EVENT;
                break;
            case "place":
                fileName = FILE_PLACE;
                break;
            case "group":
                fileName = FILE_GROUP;
                break;
            default:
                break;
        }
        return context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE).getAll();
    }

    private static class SharedPreferencesCompat
    {
        private static final Method sApplyMethod = findApplyMethod();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static Method findApplyMethod()
        {
            try             {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            }
            catch (NoSuchMethodException e) {
                Log.e("find apply method", "error");
            }
            return null;
        }

        public static void apply(SharedPreferences.Editor editor)
        {
            try             {
                if (sApplyMethod != null)
                {
                    sApplyMethod.invoke(editor);
                    return;
                }
            }
            catch (IllegalArgumentException e) {
                Log.e("apply", "error");
            }
            catch (IllegalAccessException e) {
                Log.e("apply", "error");
            }
            catch (InvocationTargetException e) {
                Log.e("apply", "error");
            }
            editor.commit();
        }
    }
}
