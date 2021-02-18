package com.algorithm.uf;

import java.util.*;

/**
 * @description: accountsMergeTest
 * @author: Taylor Qin
 * @date: 1/18/2021 7:01 PM
 **/
public class AccountsMerge {


    public static void main(String[] args) {
        AccountsMerge accountsMerge = new AccountsMerge();
        List<List<String>> accounts = new ArrayList<>();
        accounts.add(Arrays.asList("David","David0@m.co","David1@m.co"));
        accounts.add(Arrays.asList("David","David3@m.co","David4@m.co"));
        accounts.add(Arrays.asList("David","David4@m.co","David5@m.co"));
        accounts.add(Arrays.asList("David","David2@m.co","David3@m.co"));
        accounts.add(Arrays.asList("David","David1@m.co","David2@m.co"));

        List<List<String>> result = accountsMerge.accountsMerge(accounts);
        int size = result.size();


        while(true) {
            result = accountsMerge.accountsMerge(result);
            if(size == result.size()) {
                break;
            }
            size = result.size();
        }

        System.out.println(result);
    }


    public List<List<String>> accountsMerge(List<List<String>> accounts) {

        Map<String, List<Set<String>>> emailGroupsByName = new HashMap();

        for (List<String> account : accounts) {
            if(account == null || account.size() == 0) {
                continue;
            }

            String name = account.get(0);
            List<String> emails = account.subList(1, account.size());

            List<Set<String>> emailGroups = emailGroupsByName.putIfAbsent(name, new ArrayList<Set<String>>());
            emailGroups = emailGroupsByName.get(name);
            if(emailGroups.size() == 0) {
                addNewAccount(emailGroups, emails);
            } else {
                boolean merged = false;
                for (Iterator<Set<String>> i = emailGroups.iterator(); i.hasNext();) {
                    Set<String> emailGroup = i.next();
                    if(containsAny(emailGroup, emails)) {
                        emailGroup.addAll(emails);
                        merged = true;
                    }

                }
                if(!merged) {
                    addNewAccount(emailGroups, emails);
                }
            }
        }

        List<List<String>> result = new ArrayList();

        for (Map.Entry<String, List<Set<String>>> entry: emailGroupsByName.entrySet()) {
            String name = entry.getKey();
            List<Set<String>> emailGroups = entry.getValue();
            for(Set<String> emailGroup : emailGroups) {
                List<String> account = new ArrayList();
                account.add(name);
                List<String> emails = new ArrayList();
                emails.addAll(emailGroup);
                emails.sort((a,b) -> a==null? -1: a.compareTo(b));
                account.addAll(emails);
                result.add(account);
            }
        }

        return result;

    }

    public boolean containsAny(Set<String> a, List<String> b) {
        if(a==null || a.size()==0 || b==null || b.size()==0) {
            return false;
        }
        for(String s : b) {
            if(a.contains(s)) {
                return true;
            }
        }

        return false;
    }

    public void addNewAccount(List<Set<String>> list, List<String> emails) {
        Set<String> set = new HashSet();
        set.addAll(emails);
        list.add(set);
    }
}
