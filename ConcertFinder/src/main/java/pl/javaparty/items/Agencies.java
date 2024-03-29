package pl.javaparty.items;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Szymon on 2015-03-13.
 */
public enum Agencies
{
    ALTERART(0, "Alterart"),
    EBILET(0, "EBilet"),
    GOAHEAD(700, "GoAhead"),
    KAYAX(701, "Kayax"),
    LIVENATION(0, "Livenation"),
    PRESTIGE(702, "Prestige"),
    SONGKICK(800, "SongKick"),
    TICKETPRO(0, "TicketPro");

    public int fragmentNumber;
    public String toString;

    Agencies(int fnr, String str)
    {
        this.fragmentNumber = fnr;
        this.toString = str;
    }

    public static class AgenciesMethods
    {
        public static String filterAgencies(Map<Agencies, Boolean> checkedAgencies) {
            String returned = "'1'='1'";
            if(!allChecked(checkedAgencies))
            {
                returned = "'1'='0'";
                for (Agencies c : checkedAgencies.keySet())
                {
                    if (checkedAgencies.get(c))
                    {
                        returned += " OR AGENCY = '" + c.name() + "'";
                    }
                }
            }
            return returned;

        }

        private static boolean allChecked(Map<Agencies, Boolean> checkedAgencies)
        {
            boolean allchecked = true;
            for(Agencies a : checkedAgencies.keySet())
            {
                if (!checkedAgencies.get(a)) {
                    allchecked = false;
                    break;
                }
            }
            return allchecked;
        }

        public static TreeMap<Agencies, Boolean> initialize()
        {
            TreeMap<Agencies, Boolean> checkedAgencies = new TreeMap<>();
            for(Agencies a: Agencies.values())
            {
                if(a.fragmentNumber>=700)//TODO chwilowy zabieg by nie bylo widac wszystkich agencji
                    checkedAgencies.put(a, true);
            }
            return checkedAgencies;
        }
    }
}
