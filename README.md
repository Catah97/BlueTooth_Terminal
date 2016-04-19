# BlueToot Terminal
Aplikace se jmenuje BlueTooth Terminal a je ke stažení na Google Play.

https://play.google.com/store/apps/details?id=appforceone.bluetooth_terminal

#####Hlavní účel a možnosti aplikace:

Aplikace byla vytvořena za účelem poskytnout uživateli jednoduchý a univerzální způsob jak komunikovat se vzdáleným zařízení pomocí Bluetooth, pomocí bajtové komunikace. Uživatel může se vzdáleným zařízením komunikovat dvěma způsoby. 

První způsob je pomocí jednochodé textové konsole. Zde uživatel posílá do zařízení přímo bajtové hodnoty, které může zadat buď jako HEX číslici a nebo jako číslo desítkové soustavy. V konzoli uživatel dále vidí záznam poslední odchozí i příchozí komunikace s připojeným zařízením, takže je ideální pro testování nebo řešení problému s komunikací mezi vytvořeným zařízením a mobilem. 

Druhou možností komunikace je pomocí grafického ovládání, které si uživatel sám nastaví. K nastavení má k dispozici několik prvků, například: tlačítko, volant, posuvník atd. U každé položky se nastaví hodnota, kterou bude odesílat. Některé prvky mají i možnost grafické úpravy. Například na tlačítku si může upravit text, barvu textu a barvu tlačítka. Těchto ovládání si uživatel může nastavit libovolné množství a libovolně mezi nimi přepínat, upravovat a mazat. 

#### Přidání další položky do aplikace:

#####1. Přidáni položky do layout:

Nejprve je potřeba přidat položku layoutů do [set_table.xml](https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/res/layout/set_table.xml) a do [control_user.xml] (https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/res/layout/control_user.xml).     
POZOR!!! položky v těchto layoutech musí mít stejné ID.

#####2. Přidáni položky do databáze:

Poté si prvky definugjeme v [Set_Control.java] (https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/java/com/example/martin/bluetooth_terminal/SetDatabase/Set_Control.java) definujeme náší novou položku.  
Potom ji přidáme přidáme do listu, který se posílá do [DatabaseFirstRun](https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/java/com/example/martin/bluetooth_terminal/Database/DatabaseFirstRun.java)

```java 
list.add(checkbox);

databaseFirstRun = new DatabaseFirstRun(Set_Control.this, handler);
databaseFirstRun.execute(list);
```

Zde je potřeba na jaké pozici se nachází naše nová položka a podle toho upravit methodu doInBackground.
Například pokud je na pozici 50 a typ naši nové položky je CheckBox potom by kod mohl vypadat nějak takto

```java 
  protected Void doInBackground(ArrayList<Integer>... params) {
       DatabaOperations databaOperations = new DatabaOperations(context);
       ArrayList<Integer> list = params[0];
       for (int i = 0;i<list.size();i++){
           if (i<4)
               databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.VOLANT, "INVISIBLE");
          else if (i<6)
               databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.PLYN, "INVISIBLE");
          else if (i<16)
              databaOperations.PutData(String.valueOf(list.get(i)),Konstanty.SWITCH,"INVISIBLE");
           else if (i<36)
              databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.BUTTON, "INVISIBLE");
          else if (i<46)
              databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.SEEK_BAR, "INVISIBLE");
           else if (i==50)
               databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.CHECKBOX, "INVISIBLE");
          else
               databaOperations.PutData(String.valueOf(list.get(i)), Konstanty.POZNAMKY, "INVISIBLE");
      }
      return null;
  }
```
Samozřejmě je třeba vytvořit konstantu s názvem naší nové položky.

####3. Nastavení položky:

Ve třidě [Set_Control.java](https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/java/com/example/martin/bluetooth_terminal/SetDatabase/Set_Control.java) nastavíme naši položce onTouchListeners na dragandrop
```java
setOnTouchListener(dragAndDrop)
```
Ve třidě Item_Check musíte vytvořit novy list. Do tohoto listu uložte ID vaši nové položky a poté 
ve třidě SingleTapConfirm, která se nachází v [Set_Control.java](https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/java/com/example/martin/bluetooth_terminal/SetDatabase/Set_Control.java) udělat podmínku, ve které se bude spouštět vaše nastavovací activita.
Nastavovací Activitu si musíte vytvořit samy, podle potřeby vaši nové položky

####4. Přidáni rozšíření databáze a vytvoření methody na nastavení naši položky:

Je třeba upravit třidu [DatabaOperations.java](https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/java/com/example/martin/bluetooth_terminal/Database/DatabaOperations.java). Tento kro zaleži na povaze naší nové položky, takže si jej každý musí nastavit podle potřeby

#####5. Nastavení interakce s uživatelem:

V [Console.java](https://github.com/Catah97/BlueTooth_Terminal/blob/master/app/src/main/java/com/example/martin/bluetooth_terminal/Controls/Console.java) definovat naší novou položku. A přidat jí událost kdy budeme odesilat náš bajt
Například:
```java
  checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         /**Ziskáni informace z databáze jakou hodnotu je třeba udeslat*/
        DatabaOperations databaOperations = new DatabaOperations(context);
        Cursor cursor = databaOperations.GetID_Data(String.valueOf(v.getId()));
        cursor.moveToFirst();
        if (cursor.getString(5) == null)
        /**kdyby náhodou nebyla položka nastavena*/
        Toast.makeText(context, "Pro tlačítko nebyla nastavena hodnota.", Toast.LENGTH_LONG).show();
        else {
            BlueTooth.Send(cursor.getString(5));
         }
   }
});
```


Pokud uděláte všechny tyto kroky mělo by vše být bez problémů v připadě potřeby můžete napsat na email
martinrohatej@seznam.cz

